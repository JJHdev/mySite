package com.project.mySite.users;

import com.project.mySite.component.Utils.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService,AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/")
    public String home(){
        return "index";
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
    public <T> ResponseEntity login(UsersDTO usersDTO, Model model) {

        ServiceResult<UsersDTO> result = userService.login(usersDTO);
        UsersDTO userDTO = result.getData();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usersDTO.getUserId(), usersDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(result.isSuccess()){

            model.addAttribute("userJwt", userDTO.getJwt());
            return ResponseEntity.ok().body(Map.of("success" , true, "redirect" , "/", "jwtToken", usersDTO.getJwt()));
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

}

