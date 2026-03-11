package com.libraryflow.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Plain Java Test - Value Object Validierung
class ISBNTest {

    @Test
    void shouldCreateValidISBN() {
        ISBN isbn = ISBN.of("978-0-13-468599-1");

        assertThat(isbn.value()).isEqualTo("978-0-13-468599-1");
    }

    @Test
    void shouldRejectNullISBN() {
        assertThatThrownBy(() -> ISBN.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leer");
    }

    @Test
    void shouldRejectEmptyISBN() {
        assertThatThrownBy(() -> ISBN.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leer");
    }

    @Test
    void shouldRejectInvalidFormat() {
        assertThatThrownBy(() -> ISBN.of("invalid-isbn"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ungültiges ISBN-Format");
    }
}
