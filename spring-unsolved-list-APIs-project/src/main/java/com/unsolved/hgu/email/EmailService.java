package com.unsolved.hgu.email;

import com.unsolved.hgu.RedisConfig;
import com.unsolved.hgu.exception.MailRuntimeException;
import com.unsolved.hgu.util.RandomGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisConfig redisConfig;
    private String authString;
    @Value("${spring.mail.username}")
    private String username;

    public void mailSend(String setFrom, String toMail, String subject, String text) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailRuntimeException("message send fail");
        }

        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        valueOperations.set(toMail, authString, 300, TimeUnit.SECONDS);
    }

    public String joinEmail(String email) {
        authString = RandomGenerator.generateRandomString();
        String subject = "[UGH] 회원 가입 인증번호 안내";
        String content = "UGH 인증번호는 [" + authString + "] 입니다";
        mailSend(username, email, subject, content);
        return authString;
    }

    public Boolean isValidAuthString(String email, String authString) {
        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        String code = valueOperations.get(email);

        return authString.equals(code);
    }
}
