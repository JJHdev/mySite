package com.project.mySite.email;

import com.project.mySite.component.Utils.ServiceResult;
import com.project.mySite.component.exception.ValidationUserException;
import com.project.mySite.users.UserService;
import com.project.mySite.users.Users;
import com.project.mySite.users.UsersDTO;
import jakarta.mail.MessagingException;

import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public EmailController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    // 인증코드 메일 발송
    @PostMapping("/email/send")
    public ResponseEntity<String> mailSend(EmailDto emailDto) throws MessagingException {
        try{
            emailService.sendEmail(emailDto.getEmail());
            return ResponseEntity.ok("인증코드가 발송되었습니다.");
        }catch (Exception e){
            log.error("Error sending email to {}: {}", emailDto.getEmail(), e.getMessage());
            return ResponseEntity.status(500).body("Error sending email");
        }
    }

    // 인증코드 인증
    @PostMapping("/email/verify")
    public <T> ResponseEntity verify(EmailDto emailDto) {

        ServiceResult<Email> result = emailService.verifyEmailCode(emailDto);

        if(result.isSuccess()){
            Email registeredEmail = result.getData();
            return ResponseEntity.ok().body(Map.of("success" , true));
        }else{
            String errorMessage = result.getErrorMessage();
            return ResponseEntity.ok().body(Map.of("success", false, "message", errorMessage));
        }
    }

    // 인증코드 메일 발송
    @PostMapping("/email/findSend")
    public ResponseEntity<Map<String, String>> mailFindSend(@RequestBody EmailDto emailDto) throws MessagingException {
        Map<String, String> response = new HashMap<>();
        try{
            userService.validateUserIdAndUserNameAndEmail(emailDto.getUserId(),emailDto.getUserName(),emailDto.getEmail());
            emailService.sendEmail(emailDto.getEmail());
            response.put("message", "인증코드가 발송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (ValidationUserException e) {
            response.put("message", "계정정보가 없습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("message", "Error sending email");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 인증코드 인증
    @PostMapping("/email/findVerify")
    public <T> ResponseEntity findVerify(EmailDto emailDto) {

        ServiceResult<Email> result = emailService.verifyEmailCode(emailDto);
        ServiceResult<UsersDTO> result2 = userService.changePassword(emailDto);

        if(result.isSuccess() && result2.isSuccess()){
            Email registeredEmail = result.getData();
            return ResponseEntity.ok().body(Map.of("success" , true, "message",result2.getData().getPassword()));
        }else{
            String errorMessage = result.getErrorMessage();
            return ResponseEntity.ok().body(Map.of("success", false, "message", errorMessage));
        }
    }

}
