package com.libraryflow.app.catalog;

/**
 * Fachliche Ausnahme: Buch ist nicht verfügbar für die Ausleihe.
 * Domänen-Exceptions sind Teil des hexagonalen Kerns und frei von Framework-Abhängigkeiten.
 */
public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String bookTitle) {
        super("Buch '" + bookTitle + "' ist nicht verfügbar");
    }
}
