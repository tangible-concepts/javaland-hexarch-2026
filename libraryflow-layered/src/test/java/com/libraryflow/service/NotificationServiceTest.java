package com.libraryflow.service;

import com.libraryflow.infrastructure.mail.MailServiceProvider;
import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockitoBean
    private MailServiceProvider mailServiceProvider;

    @Test
    void shouldSendMailForOverdueLoan() {
        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(LocalDate.now().minusDays(21));
        loan.setDueDate(LocalDate.now().minusDays(7));
        loan.setReturned(false);

        notificationService.notifyOverdueLoans(loan);

        verify(mailServiceProvider).sendMail(
                eq("alice@example.com"),
                contains("Clean Architecture"),
                contains("Alice Schmidt"));
    }
}
