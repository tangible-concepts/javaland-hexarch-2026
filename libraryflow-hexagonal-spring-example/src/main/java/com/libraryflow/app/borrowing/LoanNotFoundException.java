package com.libraryflow.app.borrowing;

import com.libraryflow.app.shared.LoanId;

/**
 * Fachliche Ausnahme: Ausleihe wurde nicht gefunden.
 * Domänen-Exceptions sind Teil des hexagonalen Kerns und frei von Framework-Abhängigkeiten.
 */
public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(LoanId loanId) {
        super("Ausleihe mit ID " + loanId.value() + " nicht gefunden");
    }
}
