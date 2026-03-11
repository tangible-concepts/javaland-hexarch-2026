package com.libraryflow.app.shared;

/**
 * Value Object für die Identität einer Ausleihe.
 * Im hexagonalen Modell gehören Value Objects zur Domäne und sind frei von Infrastruktur-Abhängigkeiten.
 */
public record LoanId(Long value) {

    public LoanId {
        if (value == null) {
            throw new IllegalArgumentException("LoanId darf nicht null sein");
        }
    }
}
