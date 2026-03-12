package com.libraryflow.app.shared;

import com.libraryflow.app.catalog.Book;
import com.libraryflow.app.catalog.ISBN;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book createAvailableBook() {
        return new Book(
                new BookId(1L),
                new ISBN("978-0-13-468599-1"),
                "Clean Architecture",
                "Robert C. Martin",
                true
        );
    }

    private Book createBorrowedBook() {
        return new Book(
                new BookId(2L),
                new ISBN("978-0-13-235088-4"),
                "Clean Code",
                "Robert C. Martin",
                false
        );
    }

    @Test
    void shouldBorrowAvailableBook() {
        Book book = createAvailableBook();
        book.markAsBorrowed();
        assertFalse(book.isAvailable());
    }

    @Test
    void shouldNotBorrowAlreadyBorrowedBook() {
        Book book = createBorrowedBook();
        assertThrows(IllegalStateException.class, () -> book.markAsBorrowed());
    }

    @Test
    void shouldReturnBorrowedBook() {
        Book book = createBorrowedBook();
        book.returnBook();
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldNotReturnAvailableBook() {
        Book book = createAvailableBook();
        assertThrows(IllegalStateException.class, () -> book.returnBook());
    }
}
