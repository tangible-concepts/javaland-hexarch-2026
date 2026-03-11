package com.libraryflow.app.model.katalogpflege;

import com.libraryflow.app.model.Book;
import com.libraryflow.app.model.BookId;
import com.libraryflow.app.service.CatalogService;
import com.libraryflow.app.model.ISBN;
import com.libraryflow.app.ports.driven.forcatalogmanagement.BookCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private BookCatalog bookCatalog;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogService(bookCatalog);
    }

    @Test
    void createBook_happyPath() {
        ISBN isbn = new ISBN("978-0-13-468599-1");
        Book book = new Book(new BookId(1L), isbn, "Clean Architecture", "Robert C. Martin", true);
        when(bookCatalog.save(any(Book.class))).thenReturn(book);

        Book result = catalogService.createBook(isbn, "Clean Architecture", "Robert C. Martin");

        assertEquals("Clean Architecture", result.getTitle());
        assertTrue(result.isAvailable());
    }

    @Test
    void createBook_emptyTitle_throwsException() {
        ISBN isbn = new ISBN("978-0-13-468599-1");
        assertThrows(IllegalArgumentException.class,
                () -> catalogService.createBook(isbn, "", "Author"));
    }

    @Test
    void findAllBooks_returnsList() {
        Book book = new Book(new BookId(1L), new ISBN("978-0-13-468599-1"),
                "Clean Architecture", "Robert C. Martin", true);
        when(bookCatalog.findAll()).thenReturn(List.of(book));

        List<Book> result = catalogService.findAllBooks();
        assertEquals(1, result.size());
    }

    @Test
    void findAvailableBooks_returnsOnlyAvailable() {
        Book book = new Book(new BookId(1L), new ISBN("978-0-13-468599-1"),
                "Clean Architecture", "Robert C. Martin", true);
        when(bookCatalog.findAvailable()).thenReturn(List.of(book));

        List<Book> result = catalogService.findAvailableBooks();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isAvailable());
    }
}
