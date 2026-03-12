package com.libraryflow.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Plain Java Test - kein Spring, blitzschnell!
class BookTest {

    @Test
    void shouldBorrowAvailableBook() {
        Book book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);

        book.borrowTo(UserId.of(1L));

        assertThat(book.isAvailable()).isFalse();
    }

    @Test
    void shouldRejectBorrowWhenNotAvailable() {
        Book book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);

        assertThatThrownBy(() -> book.borrowTo(UserId.of(1L)))
                .isInstanceOf(BookNotAvailableException.class);
    }

    @Test
    void shouldReturnBook() {
        Book book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);

        book.returnBook();

        assertThat(book.isAvailable()).isTrue();
    }
}
