package com.libraryflow.service;

import com.libraryflow.infrastructure.mail.MailServiceProvider;
import com.libraryflow.model.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private MailServiceProvider mailServiceProvider;

    public void notifyOverdueLoans(Loan loan) {
        String to = loan.getUser().getEmail();
        String subject = "Rückgabefrist überschritten: " + loan.getBook().getTitle();
        String body = "Hallo " + loan.getUser().getName() + ", die Rückgabefrist für '"
                + loan.getBook().getTitle() + "' war am " + loan.getDueDate() + ". "
                + "Bitte geben Sie das Buch schnellstmöglich zurück.";

        mailServiceProvider.sendMail(to, subject, body);
    }
}
