package com.libraryflow.app.model;

/**
 * Fachliche Ausnahme: Buch wurde nicht gefunden.
 * Domänen-Exceptions sind Teil des hexagonalen Kerns und frei von Framework-Abhängigkeiten.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(BookId bookId) {
        super("Buch mit ID " + bookId.value() + " nicht gefunden");
    }
}
