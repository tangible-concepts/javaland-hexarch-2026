package com.libraryflow.app.shared;

import com.libraryflow.app.catalog.ISBN;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ISBNTest {

    @Test
    void shouldAcceptValidISBN() {
        ISBN isbn = new ISBN("978-0-13-468599-1");
        assertEquals("978-0-13-468599-1", isbn.value());
    }

    @Test
    void shouldAcceptAnotherValidISBN() {
        ISBN isbn = new ISBN("978-0-321-12521-7");
        assertEquals("978-0-321-12521-7", isbn.value());
    }

    @Test
    void shouldRejectNullISBN() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN(null));
    }

    @Test
    void shouldRejectBlankISBN() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN("  "));
    }

    @Test
    void shouldRejectInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN("1234567890"));
    }

    @Test
    void shouldRejectISBNWithoutProperPrefix() {
        assertThrows(IllegalArgumentException.class, () -> new ISBN("979-0-13-468599-1"));
    }
}
