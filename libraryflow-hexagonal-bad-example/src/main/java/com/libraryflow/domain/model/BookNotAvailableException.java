package com.libraryflow.domain.model;

public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(BookId bookId) {
        super("Buch mit ID " + bookId.value() + " ist nicht verfügbar");
    }
}
