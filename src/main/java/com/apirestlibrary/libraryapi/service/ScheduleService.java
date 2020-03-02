package com.apirestlibrary.libraryapi.service;

import com.apirestlibrary.libraryapi.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final static String CRON_LOANS_LATER = "0 0 0 1/1 * ?";

    @Value("${application.mail-lateloans-message}")
    private String messageMail;


    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LOANS_LATER)
    public void sendMailToLoansLater(){

        List<Loan> allLateLoans = loanService.getAllLateLoans();

        List<String> emailsList = allLateLoans.stream()
                .map(loan -> loan.getEmailCustomer())
                .collect(Collectors.toList());


        emailService.sendEmailsList(messageMail,emailsList);


    }
}
