package com.libraryflow.app.service;

import com.libraryflow.app.model.Book;
import com.libraryflow.app.model.Loan;
import com.libraryflow.app.model.User;
import com.libraryflow.app.ports.driven.forcatalogmanagement.BookCatalog;
import com.libraryflow.app.ports.driven.forcatalogmanagement.LoanManagement;
import com.libraryflow.app.ports.driven.fornotifications.NotificationSender;
import com.libraryflow.app.ports.driven.forusermanagement.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Domain Service für das Mahnwesen (Dunning).
 *
 * Prüft täglich auf überfällige Ausleihen und benachrichtigt betroffene Benutzer
 * über alle verfügbaren Benachrichtigungskanäle (Mail, Push).
 *
 * Pragmatische Ausnahme: {@code @Scheduled} wird direkt im Domain Service verwendet,
 * da ein separater Adapter für den Scheduler in diesem Kontext unverhältnismäßig wäre.
 */
public class DunningService {

    private static final Logger log = LoggerFactory.getLogger(DunningService.class);

    private final LoanManagement loanManagement;
    private final UserRepository userRepository;
    private final BookCatalog bookCatalog;
    private final List<NotificationSender> notificationSenders;

    public DunningService(LoanManagement loanManagement, UserRepository userRepository,
                          BookCatalog bookCatalog, List<NotificationSender> notificationSenders) {
        this.loanManagement = loanManagement;
        this.userRepository = userRepository;
        this.bookCatalog = bookCatalog;
        this.notificationSenders = notificationSenders;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkOverdueLoans() {
        List<Loan> overdueLoans = loanManagement.findOverdue();
        log.info("Prüfe überfällige Ausleihen: {} gefunden", overdueLoans.size());

        for (Loan loan : overdueLoans) {
            userRepository.findById(loan.getUserId()).ifPresent(user ->
                    bookCatalog.findById(loan.getBookId()).ifPresent(book ->
                            sendNotifications(user, book, loan)
                    )
            );
        }
    }

    private void sendNotifications(User user, Book book, Loan loan) {
        for (NotificationSender sender : notificationSenders) {
            sender.sendOverdueNotification(
                    user.getEmail(),
                    user.getName(),
                    book.getTitle(),
                    loan.getDueDate()
            );
        }
    }
}
