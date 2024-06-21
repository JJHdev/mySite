package com.project.mySite.email;

import com.project.mySite.UtilsComponent.ServiceResult;
import com.project.mySite.users.Users;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }



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

        System.out.printf("emailDto: %s\n", emailDto);

        ServiceResult<Email> result = emailService.verifyEmailCode(emailDto);

        if(result.isSuccess()){
            Email registeredEmail = result.getData();
            return ResponseEntity.ok().body(Map.of("success" , true, "redirect" , "/"));
        }else{
            String errorMessage = result.getErrorMessage();
            return ResponseEntity.ok().body(Map.of("success", false, "message", errorMessage));
        }
    }
}
