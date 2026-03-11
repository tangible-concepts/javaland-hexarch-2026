package com.libraryflow.infrastructure.mail;

public interface MailServiceProvider {

    void sendMail(String to, String subject, String body);
}
