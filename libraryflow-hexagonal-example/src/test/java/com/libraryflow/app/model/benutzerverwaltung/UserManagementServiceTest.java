package com.libraryflow.app.model.benutzerverwaltung;

import com.libraryflow.app.model.*;
import com.libraryflow.app.service.UserManagementService;
import com.libraryflow.app.ports.driven.forcatalogmanagement.LoanManagement;
import com.libraryflow.app.ports.driven.forusermanagement.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoanManagement loanManagement;

    private UserManagementService service;

    @BeforeEach
    void setUp() {
        service = new UserManagementService(userRepository, loanManagement);
    }

    @Test
    void createUser_happyPath() {
        User user = new User(new UserId(1L), "Alice", "alice@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = service.createUser("Alice", "alice@example.com");

        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void createUser_emptyName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.createUser("", "alice@example.com"));
    }

    @Test
    void createUser_emptyEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.createUser("Alice", ""));
    }

    @Test
    void findAllUsers_returnsList() {
        User user = new User(new UserId(1L), "Alice", "alice@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = service.findAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void findUserDetailById_withActiveLoans() {
        UserId userId = new UserId(1L);
        User user = new User(userId, "Alice", "alice@example.com");
        Loan loan = Loan.reconstitute(1L, new BookId(1L), userId,
                LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanManagement.findActiveByUserId(userId)).thenReturn(List.of(loan));

        UserDetail detail = service.findUserDetailById(userId);

        assertEquals(1, detail.activeLoans());
        assertEquals(0, detail.overdueLoans());
        assertEquals(2, detail.remainingBorrowSlots());
    }

    @Test
    void findUserDetailById_withOverdueLoans() {
        UserId userId = new UserId(1L);
        User user = new User(userId, "Alice", "alice@example.com");
        Loan overdueLoan = Loan.reconstitute(1L, new BookId(1L), userId,
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(16), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanManagement.findActiveByUserId(userId)).thenReturn(List.of(overdueLoan));

        UserDetail detail = service.findUserDetailById(userId);

        assertEquals(1, detail.overdueLoans());
    }

    @Test
    void findUserDetailById_userNotFound_throwsException() {
        UserId userId = new UserId(99L);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findUserDetailById(userId));
    }
}
