package com.libraryflow.domain.model;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(BookId bookId) {
        super("Buch mit ID " + bookId.value() + " nicht gefunden");
    }
}
