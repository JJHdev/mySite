package com.project.mySite.users;

import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.Utils.ServiceResult;
import com.project.mySite.component.security.MyUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
    }



    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/charts")
    public String home2(){
        return "charts";
    }

    @GetMapping("/user/register")
    public String register(){
        return "users/register";
    }

    @PostMapping("/user/register")
    public <T> ResponseEntity register(UsersDTO usersDTO){
        ServiceResult<Users> result = userService.register(usersDTO);
        if(result.isSuccess()){
            Users registeredUser = result.getData();
            return ResponseEntity.ok().body(Map.of("success" , true, "redirect" , "/"));
        }else{
            String errorMessage = result.getErrorMessage();
            return ResponseEntity.ok().body(Map.of("success", false, "message", errorMessage));
        }
    }

    @GetMapping("/user/login")
    public String login(){
        return "users/login";
    }

    @PostMapping("/user/login")
    public <T> ResponseEntity login(UsersDTO usersDTO, HttpServletResponse response) throws IOException {

        ServiceResult<UsersDTO> result = userService.login(usersDTO);
        UsersDTO userDTO = result.getData();

        if(result.isSuccess()){
            String accessToken = userDTO.getAccessToken();
            String refreshToken = userDTO.getRefreshToken();

            // Set JWT in cookie
            addCookie(response, "accessToken", accessToken, 10); // 10초
            addCookie(response, "refreshToken", refreshToken, 30 * 60); // 30분

            return ResponseEntity.ok().body(Map.of("success", true, "redirect", "/"));
        }else{
            String errorMessage = result.getErrorMessage();
            return ResponseEntity.ok().body(Map.of("success", false, "message", errorMessage));
        }
    }

    @PostMapping("/user/checkUser")
    public <T> ResponseEntity checkUser(UsersDTO usersDTO){

        ServiceResult<Users> result = userService.checkUser(usersDTO);

        if(result.isSuccess()){
            Users resultUser = result.getData();
            return ResponseEntity.ok().body(Map.of("success" , true, "message" , resultUser.getUserId() +"의 아이디는 사용하실수 있습니다."));
        }else{
            String errorMessage = result.getErrorMessage();
            return ResponseEntity.ok().body(Map.of("success", false, "message", errorMessage));
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null && !jwtUtil.isTokenExpired(refreshToken)) {
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateAccessToken(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

            addCookie(response, "accessToken", newAccessToken, 10); // 10초
        } else {
            invalidateCookie(response, "refreshToken");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token has expired. Please login again.");
        }
    }

    private void invalidateCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}

