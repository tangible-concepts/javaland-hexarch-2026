package com.libraryflow.drivingadapter.rest;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookDetail;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.ISBN;
import com.libraryflow.domain.model.BookNotFoundException;
import com.libraryflow.domain.drivingport.FindBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock gegen Interface, nicht gegen Implementierung!
    @MockitoBean
    private FindBook findBook;

    @Test
    void shouldReturnAllBooks() throws Exception {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);

        when(findBook.findAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"))
                .andExpect(jsonPath("$[0].isbn").value("978-0-13-468599-1"));
    }

    @Test
    void shouldReturnAvailableBookDetailById() throws Exception {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        var detail = BookDetail.available(book);

        when(findBook.findBookDetailById(BookId.of(1L))).thenReturn(detail);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.borrowedBy").isEmpty());
    }

    @Test
    void shouldReturnBorrowedBookDetailWithLoanInfo() throws Exception {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);
        var detail = BookDetail.borrowed(book, "Alice", LocalDate.now().plusDays(7), 7);

        when(findBook.findBookDetailById(BookId.of(1L))).thenReturn(detail);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.borrowedBy").value("Alice"))
                .andExpect(jsonPath("$.daysRemaining").value(7));
    }

    @Test
    void shouldReturnAvailableBooks() throws Exception {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);

        when(findBook.findAvailableBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void shouldSearchBooksByTitle() throws Exception {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);

        when(findBook.searchBooks("Clean", null, null)).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/search").param("title", "Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"));
    }

    @Test
    void shouldSearchBooksWithAuthorFilter() throws Exception {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);

        when(findBook.searchBooks("Clean", "Martin", null)).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/search").param("title", "Clean").param("author", "Martin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));
    }

    @Test
    void shouldSearchBooksWithAvailabilityFilter() throws Exception {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);

        when(findBook.searchBooks("Clean", null, true)).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/search").param("title", "Clean").param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(findBook.findBookDetailById(BookId.of(99L)))
                .thenThrow(new BookNotFoundException(BookId.of(99L)));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }
}
