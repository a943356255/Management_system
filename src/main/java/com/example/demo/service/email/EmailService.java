package com.example.demo.service.email;

public interface EmailService {

    void sendSimpleMail(String from, String to, String subject, String text);

}
