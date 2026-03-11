package com.libraryflow.service;

import com.libraryflow.model.Book;
import com.libraryflow.infrastructure.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Schmerzpunkt: @SpringBootTest erforderlich - ganzer Spring-Kontext wird hochgefahren
// Schmerzpunkt: Langsamer Teststart (~3-5 Sekunden)
@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    // Schmerzpunkt: @MockitoBean für jedes Repository
    @MockitoBean
    private BookRepository bookRepository;

    @Test
    void shouldReturnAllBooks() {
        // Aufwändiges Setup mit JPA-Entities
        Book book1 = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book1.setId(1L);
        Book book2 = new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin");
        book2.setId(2L);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> books = bookService.getAllBooks();

        assertThat(books).hasSize(2);
        assertThat(books.get(0).getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldFindBookById() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertThat(result.getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Schmerzpunkt: Test muss HTTP-spezifische Exception prüfen
        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nicht gefunden");
    }

    @Test
    void shouldCreateBook() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.createBook(book);

        assertThat(result.getTitle()).isEqualTo("Clean Architecture");
        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    void shouldRejectBookWithoutIsbn() {
        Book book = new Book();
        book.setTitle("Test");

        assertThatThrownBy(() -> bookService.createBook(book))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ISBN");
    }

    @Test
    void shouldRejectBookWithoutTitle() {
        Book book = new Book();
        book.setIsbn("978-0-13-468599-1");

        assertThatThrownBy(() -> bookService.createBook(book))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Titel");
    }

    @Test
    void shouldSearchBooksByTitle() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookRepository.findByTitleContainingIgnoreCase("Clean")).thenReturn(List.of(book));

        List<Book> results = bookService.searchBooks("Clean");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldReturnAvailableBooks() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);

        when(bookRepository.findByAvailableTrue()).thenReturn(List.of(book));

        List<Book> results = bookService.getAvailableBooks();

        assertThat(results).hasSize(1);
    }
}
