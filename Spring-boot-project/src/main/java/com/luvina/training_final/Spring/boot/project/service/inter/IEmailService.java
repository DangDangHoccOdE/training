package com.luvina.training_final.Spring.boot.project.service.inter;

public interface IEmailService {
    void sendMessage(String from, String to, String subject, String text);
}
