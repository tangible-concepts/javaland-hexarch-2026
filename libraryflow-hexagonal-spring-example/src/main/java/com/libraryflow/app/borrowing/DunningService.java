package com.libraryflow.app.borrowing;

import com.libraryflow.app.catalog.Book;
import com.libraryflow.app.catalog.BookCatalog;
import com.libraryflow.app.users.User;
import com.libraryflow.app.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain Service für das Mahnwesen (Dunning).
 *
 * Prüft täglich auf überfällige Ausleihen und benachrichtigt betroffene Benutzer
 * über alle verfügbaren Benachrichtigungskanäle (Mail, Push).
 *
 * @see <a href="docs/adr/001-spring-stereotype-annotations.md">ADR-001: Spring Stereotype-Annotationen</a>
 * @see <a href="docs/adr/003-scheduled-im-domain-service.md">ADR-003: @Scheduled im Domain Service</a>
 */
@Service // ADR-001
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
