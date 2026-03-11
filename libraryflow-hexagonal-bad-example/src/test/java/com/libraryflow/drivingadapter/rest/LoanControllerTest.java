package com.libraryflow.drivingadapter.rest;

import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.BookNotAvailableException;
import com.libraryflow.domain.model.BookNotFoundException;
import com.libraryflow.domain.model.BorrowLimitExceededException;
import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.BorrowBookCommand;
import com.libraryflow.domain.drivingport.BorrowBook;
import com.libraryflow.domain.drivingport.ManageUser;
import com.libraryflow.domain.drivingport.ReturnBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    private BorrowBook borrowBook;

    @MockitoBean
    private ReturnBook returnBook;

    @MockitoBean
    private ManageUser manageUser;

    @Test
    void shouldBorrowBook() throws Exception {
        var loan = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now(), LocalDate.now().plusDays(14), false);
        when(borrowBook.borrowBook(any(BorrowBookCommand.class))).thenReturn(loan);

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\": 1, \"userId\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.returned").value(false));
    }

    @Test
    void shouldReturnBook() throws Exception {
        var loan = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), true);
        when(returnBook.returnBook(1L)).thenReturn(loan);

        mockMvc.perform(post("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned").value(true));
    }

    @Test
    void shouldReturnActiveLoansForUser() throws Exception {
        var loan = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now().minusDays(3), LocalDate.now().plusDays(11), false);

        when(manageUser.findActiveLoansForUser(UserId.of(1L))).thenReturn(List.of(loan));

        mockMvc.perform(get("/api/loans/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].returned").value(false));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(borrowBook.borrowBook(any())).thenThrow(new BookNotFoundException(BookId.of(99L)));

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\": 99, \"userId\": 1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenBookNotAvailable() throws Exception {
        when(borrowBook.borrowBook(any())).thenThrow(new BookNotAvailableException(BookId.of(1L)));

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\": 1, \"userId\": 1}"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409WhenBorrowLimitExceeded() throws Exception {
        when(borrowBook.borrowBook(any())).thenThrow(new BorrowLimitExceededException(UserId.of(1L)));

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\": 1, \"userId\": 1}"))
                .andExpect(status().isConflict());
    }
}
