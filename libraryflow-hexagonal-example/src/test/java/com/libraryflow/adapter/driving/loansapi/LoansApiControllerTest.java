package com.libraryflow.adapter.driving.loansapi;

import com.libraryflow.app.model.*;
import com.libraryflow.app.ports.driving.forloans.BorrowCommand;
import com.libraryflow.app.ports.driving.forloans.ForLoans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoansApiController.class)
class LoansApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForLoans forLoans;

    @Test
    void borrowBook_returnsCreated() throws Exception {
        Loan loan = Loan.reconstitute(1L, new BookId(1L), new UserId(1L),
                LocalDate.now(), LocalDate.now().plusDays(14), false);
        when(forLoans.borrowBook(any(BorrowCommand.class))).thenReturn(loan);

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":1,\"userId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.returned").value(false));
    }

    @Test
    void borrowBook_bookNotFound_returns404() throws Exception {
        when(forLoans.borrowBook(any())).thenThrow(new BookNotFoundException(new BookId(99L)));

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":99,\"userId\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowBook_notAvailable_returns409() throws Exception {
        when(forLoans.borrowBook(any())).thenThrow(new BookNotAvailableException("Test Book"));

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":1,\"userId\":1}"))
                .andExpect(status().isConflict());
    }

    @Test
    void returnBook_returnsOk() throws Exception {
        Loan loan = Loan.reconstitute(1L, new BookId(1L), new UserId(1L),
                LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), true);
        when(forLoans.returnBook(1L)).thenReturn(loan);

        mockMvc.perform(post("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned").value(true));
    }

    @Test
    void findBookDetailById_returnsDetail() throws Exception {
        BookDetail detail = new BookDetail(
                new BookId(1L), new ISBN("978-0-13-468599-1"),
                "Clean Architecture", "Robert C. Martin",
                true, null, null, 0);
        when(forLoans.findBookDetailById(any())).thenReturn(detail);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void searchBooks_returnsList() throws Exception {
        Book book = new Book(new BookId(1L), new ISBN("978-0-13-468599-1"),
                "Clean Architecture", "Robert C. Martin", true);
        when(forLoans.searchBooks("Clean")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/search").param("title", "Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"));
    }

    @Test
    void findAvailableBooks_returnsList() throws Exception {
        Book book = new Book(new BookId(1L), new ISBN("978-0-13-468599-1"),
                "Clean Architecture", "Robert C. Martin", true);
        when(forLoans.findAvailableBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void findActiveLoansForUser_returnsList() throws Exception {
        Loan loan = Loan.reconstitute(1L, new BookId(1L), new UserId(1L),
                LocalDate.now(), LocalDate.now().plusDays(14), false);
        when(forLoans.findActiveLoansForUser(any())).thenReturn(List.of(loan));

        mockMvc.perform(get("/api/loans/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
