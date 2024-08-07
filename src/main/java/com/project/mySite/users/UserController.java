package com.project.mySite.users;

import com.project.mySite.component.Utils.ServiceResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.io.IOException;
import java.util.Map;

@Controller
public class UserController {

    @Value("${jwt.accessExp}")
    private long ACCESS_TOKEN_TIME;
    @Value("${jwt.refreshExp}")
    private long REFRESH_TOKEN_TIME;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

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
    public <T> ResponseEntity login(UsersDTO usersDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {

        ServiceResult<UsersDTO> result = userService.login(usersDTO, request);
        UsersDTO userDTO = result.getData();

        if(result.isSuccess()){
            addAccessToekn(response, "accessToken", userDTO.getAccessToken(), (int) ACCESS_TOKEN_TIME / 1000);
            addRefreshToken(response, "refreshToken", userDTO.getRefreshToken(), (int) REFRESH_TOKEN_TIME / 1000);
            return ResponseEntity.ok().body(Map.of("success", true,"redirect", "/"));
        }else{
            return ResponseEntity.ok().body(Map.of("success", false, "message", result.getErrorMessage()));
        }
    }

    @GetMapping("/user/findPassword")
    public String findPassword(){
        return "users/findPassword";
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

    private void addAccessToekn(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void addRefreshToken(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}

