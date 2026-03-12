package com.libraryflow.app.model;

/**
 * Fachliche Ausnahme: Ausleihe wurde nicht gefunden.
 * Domänen-Exceptions sind Teil des hexagonalen Kerns und frei von Framework-Abhängigkeiten.
 */
public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(Long loanId) {
        super("Ausleihe mit ID " + loanId + " nicht gefunden");
    }
}
