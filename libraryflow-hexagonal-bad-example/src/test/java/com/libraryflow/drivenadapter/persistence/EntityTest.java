package com.libraryflow.drivenadapter.persistence;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void bookEntitySetters() {
        var entity = new BookEntity();
        entity.setId(1L);
        entity.setIsbn("978-0-13-468599-1");
        entity.setTitle("Clean Architecture");
        entity.setAuthor("Robert C. Martin");
        entity.setAvailable(true);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getIsbn()).isEqualTo("978-0-13-468599-1");
        assertThat(entity.getTitle()).isEqualTo("Clean Architecture");
        assertThat(entity.getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(entity.isAvailable()).isTrue();
    }

    @Test
    void userEntitySetters() {
        var entity = new UserEntity();
        entity.setId(1L);
        entity.setName("Alice");
        entity.setEmail("alice@example.com");

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Alice");
        assertThat(entity.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void loanEntitySetters() {
        var entity = new LoanEntity();
        entity.setId(1L);
        entity.setBookId(2L);
        entity.setUserId(3L);
        entity.setBorrowDate(LocalDate.of(2026, 1, 1));
        entity.setDueDate(LocalDate.of(2026, 1, 15));
        entity.setReturned(false);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getBookId()).isEqualTo(2L);
        assertThat(entity.getUserId()).isEqualTo(3L);
        assertThat(entity.getBorrowDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(entity.getDueDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(entity.isReturned()).isFalse();
    }
}
