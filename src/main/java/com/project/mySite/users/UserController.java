package com.project.mySite.users;

import com.project.mySite.UtilsComponent.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(){
        return "users/login";
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

}

