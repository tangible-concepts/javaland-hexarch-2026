package com.libraryflow.app.borrowing;

/**
 * Fachliche Ausnahme: Ausleihlimit überschritten oder Benutzer hat überfällige Ausleihen.
 * Domänen-Exceptions sind Teil des hexagonalen Kerns und frei von Framework-Abhängigkeiten.
 */
public class BorrowLimitExceededException extends RuntimeException {

    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
