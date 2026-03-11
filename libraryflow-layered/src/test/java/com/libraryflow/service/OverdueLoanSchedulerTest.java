package com.libraryflow.service;

import com.libraryflow.infrastructure.repository.LoanRepository;
import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OverdueLoanSchedulerTest {

    @Autowired
    private OverdueLoanScheduler overdueLoanScheduler;

    @MockitoBean
    private LoanRepository loanRepository;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void shouldNotifyForEachOverdueLoan() {
        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        Book book1 = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book1.setId(1L);
        Book book2 = new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin");
        book2.setId(2L);

        Loan loan1 = new Loan();
        loan1.setId(1L);
        loan1.setBook(book1);
        loan1.setUser(user);
        loan1.setDueDate(LocalDate.now().minusDays(3));

        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setBook(book2);
        loan2.setUser(user);
        loan2.setDueDate(LocalDate.now().minusDays(1));

        when(loanRepository.findByReturnedFalseAndDueDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(loan1, loan2));

        overdueLoanScheduler.checkOverdueLoans();

        verify(notificationService).notifyOverdueLoans(loan1);
        verify(notificationService).notifyOverdueLoans(loan2);
    }

    @Test
    void shouldNotNotifyWhenNoOverdueLoans() {
        when(loanRepository.findByReturnedFalseAndDueDateBefore(any(LocalDate.class)))
                .thenReturn(List.of());

        overdueLoanScheduler.checkOverdueLoans();

        verify(notificationService, never()).notifyOverdueLoans(any());
    }
}
