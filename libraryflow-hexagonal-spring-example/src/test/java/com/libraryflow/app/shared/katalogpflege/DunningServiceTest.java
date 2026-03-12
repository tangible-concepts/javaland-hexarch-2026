package com.libraryflow.app.shared.katalogpflege;

import com.libraryflow.app.borrowing.*;
import com.libraryflow.app.catalog.*;
import com.libraryflow.app.shared.*;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.LoanId;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DunningServiceTest {

    @Mock
    private LoanManagement loanManagement;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookCatalog bookCatalog;
    @Mock
    private NotificationSender mailSender;
    @Mock
    private NotificationSender pushSender;

    private DunningService dunningService;

    @BeforeEach
    void setUp() {
        dunningService = new DunningService(loanManagement, userRepository, bookCatalog,
                List.of(mailSender, pushSender));
    }

    @Test
    void checkOverdueLoans_sendsNotificationsViaBothChannels() {
        BookId bookId = new BookId(1L);
        UserId userId = new UserId(1L);
        Loan overdueLoan = Loan.reconstitute(new LoanId(1L), bookId, userId,
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(16), false);
        User user = new User(userId, "Alice", "alice@example.com");
        Book book = new Book(bookId, new ISBN("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);

        when(loanManagement.findOverdue()).thenReturn(List.of(overdueLoan));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookCatalog.findById(bookId)).thenReturn(Optional.of(book));

        dunningService.checkOverdueLoans();

        verify(mailSender).sendOverdueNotification("alice@example.com", "Alice",
                "Clean Architecture", overdueLoan.getDueDate());
        verify(pushSender).sendOverdueNotification("alice@example.com", "Alice",
                "Clean Architecture", overdueLoan.getDueDate());
    }

    @Test
    void checkOverdueLoans_noOverdueLoans_sendsNoNotifications() {
        when(loanManagement.findOverdue()).thenReturn(List.of());

        dunningService.checkOverdueLoans();

        verifyNoInteractions(mailSender);
        verifyNoInteractions(pushSender);
    }
}
