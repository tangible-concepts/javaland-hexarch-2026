package com.libraryflow.app.service;

import com.libraryflow.app.administration.AdministrationService;
import com.libraryflow.app.administration.UserDetail;
import com.libraryflow.app.borrowing.Loan;
import com.libraryflow.app.borrowing.LoanManagement;
import com.libraryflow.app.catalog.CatalogService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdministrationServiceTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private UserManagementService userManagementService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoanManagement loanManagement;

    private AdministrationService facade;

    @BeforeEach
    void setUp() {
        facade = new AdministrationService(catalogService, userManagementService, userRepository, loanManagement);
    }

    @Test
    void findUserDetailById_withActiveLoans() {
        UserId userId = new UserId(1L);
        User user = new User(userId, "Alice", "alice@example.com");
        Loan loan = Loan.reconstitute(new LoanId(1L), new BookId(1L), userId,
                LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanManagement.findActiveByUserId(userId)).thenReturn(List.of(loan));

        UserDetail detail = facade.findUserDetailById(userId);

        assertEquals(1, detail.activeLoans());
        assertEquals(0, detail.overdueLoans());
        assertEquals(2, detail.remainingBorrowSlots());
    }

    @Test
    void findUserDetailById_withOverdueLoans() {
        UserId userId = new UserId(1L);
        User user = new User(userId, "Alice", "alice@example.com");
        Loan overdueLoan = Loan.reconstitute(new LoanId(1L), new BookId(1L), userId,
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(16), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanManagement.findActiveByUserId(userId)).thenReturn(List.of(overdueLoan));

        UserDetail detail = facade.findUserDetailById(userId);

        assertEquals(1, detail.overdueLoans());
    }

    @Test
    void findUserDetailById_userNotFound_throwsException() {
        UserId userId = new UserId(99L);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> facade.findUserDetailById(userId));
    }
}
