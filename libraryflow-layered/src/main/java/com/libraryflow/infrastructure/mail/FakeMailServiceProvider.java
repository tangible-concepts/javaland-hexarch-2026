package com.libraryflow.infrastructure.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FakeMailServiceProvider implements MailServiceProvider {

    private static final Logger log = LoggerFactory.getLogger(FakeMailServiceProvider.class);

    @Override
    public void sendMail(String to, String subject, String body) {
        log.info("Sending mail to: {}, subject: '{}', body: '{}'", to, subject, body);
    }
}
