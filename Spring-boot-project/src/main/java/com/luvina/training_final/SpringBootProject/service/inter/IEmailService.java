package com.luvina.training_final.SpringBootProject.service.inter;

public interface IEmailService {
    void sendMessage(String from, String to, String subject, String text);
}
