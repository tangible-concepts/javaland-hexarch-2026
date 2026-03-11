package com.libraryflow.controller;

import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import com.libraryflow.service.LoanService;
import com.libraryflow.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private LoanService loanService;

    @Test
    void shouldReturnAllUsers() throws Exception {
        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice Schmidt"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void shouldReturnUserByIdWithLoanStats() throws Exception {
        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        Loan activeLoan = createLoan(1L, LocalDate.now().plusDays(7));
        Loan overdueLoan = createLoan(2L, LocalDate.now().minusDays(2));

        when(userService.getUserById(1L)).thenReturn(user);
        when(loanService.getActiveLoansForUser(1L)).thenReturn(List.of(activeLoan, overdueLoan));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Schmidt"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.activeLoans").value(2))
                .andExpect(jsonPath("$.overdueLoans").value(1))
                .andExpect(jsonPath("$.remainingBorrowSlots").value(1));
    }

    @Test
    void shouldReturnUserWithNoLoans() throws Exception {
        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);
        when(loanService.getActiveLoansForUser(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Schmidt"))
                .andExpect(jsonPath("$.activeLoans").value(0))
                .andExpect(jsonPath("$.overdueLoans").value(0))
                .andExpect(jsonPath("$.remainingBorrowSlots").value(3));
    }

    @Test
    void shouldCreateUser() throws Exception {
        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Alice Schmidt\", \"email\": \"alice@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice Schmidt"));
    }

    private Loan createLoan(Long bookId, LocalDate dueDate) {
        Book book = new Book("978-0-13-468599-1", "Test Book", "Test Author");
        book.setId(bookId);

        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        Loan loan = new Loan();
        loan.setId(bookId);
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(LocalDate.now().minusDays(7));
        loan.setDueDate(dueDate);
        loan.setReturned(false);
        return loan;
    }
}
