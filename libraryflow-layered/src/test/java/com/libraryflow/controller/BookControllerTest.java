package com.libraryflow.controller;

import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import com.libraryflow.service.BookService;
import com.libraryflow.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private LoanService loanService;

    @Test
    void shouldReturnAllBooks() throws Exception {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));
    }

    @Test
    void shouldReturnAvailableBookById() throws Exception {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.borrowedBy").doesNotExist());
    }

    @Test
    void shouldReturnBorrowedBookWithLoanInfo() throws Exception {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);
        book.setAvailable(false);

        User user = new User("Bob Müller", "bob@example.com");
        user.setId(2L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(10));
        loan.setReturned(false);

        when(bookService.getBookById(1L)).thenReturn(book);
        when(loanService.getActiveLoansForBook(1L)).thenReturn(List.of(loan));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.borrowedBy").value("Bob Müller"))
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusDays(10).toString()))
                .andExpect(jsonPath("$.daysRemaining").value(10));
    }

    @Test
    void shouldReturnAvailableBooks() throws Exception {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookService.getAvailableBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void shouldSearchBooksByTitle() throws Exception {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookService.searchBooks("Clean")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/search").param("title", "Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"));
    }

    @Test
    void shouldFilterSearchByAuthor() throws Exception {
        Book book1 = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book1.setId(1L);
        Book book2 = new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin");
        book2.setId(2L);
        Book book3 = new Book("978-3-86490-484-2", "Clean Agile", "Other Author");
        book3.setId(3L);

        when(bookService.searchBooks("Clean")).thenReturn(List.of(book1, book2, book3));

        mockMvc.perform(get("/api/books/search")
                        .param("title", "Clean")
                        .param("author", "Robert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));
    }

    @Test
    void shouldFilterSearchByAvailability() throws Exception {
        Book book1 = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book1.setId(1L);
        book1.setAvailable(true);
        Book book2 = new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin");
        book2.setId(2L);
        book2.setAvailable(false);

        when(bookService.searchBooks("Clean")).thenReturn(List.of(book1, book2));

        mockMvc.perform(get("/api/books/search")
                        .param("title", "Clean")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"));
    }

    @Test
    void shouldCreateBook() throws Exception {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"978-0-13-468599-1\", \"title\": \"Clean Architecture\", \"author\": \"Robert C. Martin\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.isbn").value("978-0-13-468599-1"));
    }
}
