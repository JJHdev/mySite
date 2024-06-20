package com.project.mySite.users;

import com.project.mySite.UtilsComponent.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public <T> ResponseEntity register(UsersForm usersForm){

        System.out.printf("userForm: %s\n", usersForm);

        ServiceResult<Users> result = userService.register(usersForm);

        if(result.isSuccess()){
            Users registeredUser = result.getData();
            return ResponseEntity.ok().body(Map.of("success" , true, "redirect" , "/"));
        }else{
            String errorMessage = result.getErrorMessage();
            return ResponseEntity.ok().body(Map.of("success", false, "message", errorMessage));
        }

    }

    //이메일 인증
    @GetMapping("/user/emailCheck")
    @ResponseBody
    public String mailCheck(String email) {
        System.out.println("이메일 인증 요청이 들어옴!");
        System.out.println("이메일 인증 이메일 : " + email);
        return "인증요청옴";
    }
}

