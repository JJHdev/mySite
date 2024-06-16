package com.project.mySite.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class userController {

    @GetMapping("/")
    public String home(){
        return "users/login";
    }

    @GetMapping("/user/register")
    public String register(){
        return "users/register";
    }

    @PostMapping("/user/register")
    public String register(Users users){
        System.out.printf("user: %s%n", users);
        return "users/register";
    }
}

