package com.project.mySite.email;

import com.project.mySite.component.Utils.ServiceResult;
import com.project.mySite.component.exception.EmailCheckException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.sender-email}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, EmailRepository emailRepository) {
        this.javaMailSender = javaMailSender;
        this.emailRepository = emailRepository;
    }

    // 이메일 발송 메서드
    public void sendEmail(String toEmail) throws MessagingException {
        Optional<Email> existingEmail = emailRepository.findByEmail(toEmail);
        existingEmail.ifPresent(emailRepository::delete);

        // 인증 코드 생성
        String code = generateVerificationCode();

        // 인증 정보 저장
        Email email = Email.builder()
                .email(toEmail)
                .code(code)
                .expiration(LocalDateTime.now().plusMinutes(30)) // 30분 유효기간
                .build();
        emailRepository.save(email);

        // 이메일 폼 생성
        MimeMessage emailForm = createEmailForm(toEmail, code);

        // 이메일 발송
        javaMailSender.send(emailForm);
    }

    private MimeMessage createEmailForm(String toEmail, String code) throws MessagingException {
        // 이메일 폼 작성 로직
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setSubject("안녕하세요. 인증번호입니다.");
        message.setFrom(senderEmail);
        message.setText(setContext(code), "utf-8", "html");
        message.setRecipients(MimeMessage.RecipientType.TO, toEmail);

        return message;
    }

    // 이메일 내용 초기화
    private String setContext(String code) {
        Context context = new Context();
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        context.setVariable("code", code);

        templateResolver.setPrefix("templates/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process("mail", context);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(999999);
        return String.format("%06d", code);
    }

    // 코드 검증
    public ServiceResult<Email> verifyEmailCode(EmailDto emailDto) {
        Optional<Email> emailOptional = emailRepository.findByEmail(emailDto.getEmail());

        //유효성검사
        try {
            if (emailOptional.isEmpty()) {
                throw new EmailCheckException("인증한 메일의 주소가 다릅니다.");
            }
            // 만료시간이 초과되었는지
            Email email = emailOptional.get();
            if(LocalDateTime.now().isAfter(email.getExpiration())){
                throw new EmailCheckException("인증만료시간이 초과되었습니다.");
            }
            // 코드가 지났는지
            if(!email.getCode().equals(emailDto.getCode())){
                throw new EmailCheckException("인증번호가 다릅니다.");
            }

            email.setEmailStatus(true);
            Email savedEmail = emailRepository.save(email);
            return ServiceResult.success(savedEmail);

        } catch (EmailCheckException e){
            return ServiceResult.failure(e.getMessage());
        } catch (Exception e) {
            return ServiceResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

}
