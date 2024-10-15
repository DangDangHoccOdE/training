package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.impl.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EmailServiceTest {
    @InjectMocks
    private EmailService emailService;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private MimeMessageHelper mimeMessageHelper;

    String from = "a@gmai.com";
    String to = "b@gmai.com";
    String subject = "test";
    String body = "test";

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmail_Success(){
        doNothing().when(mailSender).send(mimeMessage);

        emailService.sendMessage(from, to, subject, body);

        verify(mailSender,times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmail_Failure() throws MessagingException {
        doThrow(new MessagingException("Error! Unable to send Email")).when(mimeMessage).setSubject(anyString());

        assertThrows(CustomException.class,()->emailService.sendMessage(from,to,subject,body));

    }

}
