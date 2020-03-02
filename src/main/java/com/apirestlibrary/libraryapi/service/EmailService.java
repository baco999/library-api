package com.apirestlibrary.libraryapi.service;

import java.util.List;

public interface EmailService {

    void sendEmailsList(String messageMail, List<String> emailsList);
}
