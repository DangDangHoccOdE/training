package com.luvina.training_final.SpringBootProject.service.impl;

import com.luvina.training_final.SpringBootProject.exception.CustomException;
import com.luvina.training_final.SpringBootProject.service.inter.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMessage(String from, String to, String subject, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text,true);
        }catch (MessagingException e){
            throw new CustomException("Error! Unable to send Email", HttpStatus.BAD_REQUEST);
        }
        javaMailSender.send(mimeMessage);
    }
}
