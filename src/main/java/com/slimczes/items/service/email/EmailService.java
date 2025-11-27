package com.slimczes.items.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Async
    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to: {}, subject: {}, body: {}", to, subject, body);
    }
}
