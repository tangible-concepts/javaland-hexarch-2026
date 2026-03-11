package com.libraryflow.controller;

import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import com.libraryflow.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @Test
    void shouldBorrowBook() throws Exception {
        Loan loan = createTestLoan();

        when(loanService.borrowBook(1L, 2L)).thenReturn(loan);

        mockMvc.perform(post("/api/loans/borrow")
                        .param("bookId", "1")
                        .param("userId", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.returned").value(false));
    }

    @Test
    void shouldReturnBook() throws Exception {
        Loan loan = createTestLoan();
        loan.setReturned(true);

        when(loanService.returnBook(1L)).thenReturn(loan);

        mockMvc.perform(post("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned").value(true));
    }

    @Test
    void shouldReturnActiveLoansForUser() throws Exception {
        Loan loan = createTestLoan();

        when(loanService.getActiveLoansForUser(2L)).thenReturn(List.of(loan));

        mockMvc.perform(get("/api/loans/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.title").value("Clean Architecture"));
    }

    private Loan createTestLoan() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        User user = new User("Bob Müller", "bob@example.com");
        user.setId(2L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setReturned(false);
        return loan;
    }
}
