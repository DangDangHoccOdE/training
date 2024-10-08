package com.hoanghaidang.social_network.service.inter;

public interface IEmailService {
    void sendMessage(String from, String to, String subject, String text);
}
