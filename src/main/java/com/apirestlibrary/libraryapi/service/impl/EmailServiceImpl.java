package com.apirestlibrary.libraryapi.service.impl;

import com.apirestlibrary.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${application.mail-default-remetent}")
    private String remetentMail;

    @Override
    public void sendEmailsList(String messageMail, List<String> emailsList) {

        String[] arraysMail = emailsList.toArray(new String[emailsList.size()]);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(remetentMail);
        simpleMailMessage.setSubject("Livro com Empréstimo atrasado");
        simpleMailMessage.setText(messageMail);
        simpleMailMessage.setTo(arraysMail);

        javaMailSender.send(simpleMailMessage);

    }
}
