package com.libraryflow.app.model;

import java.util.List;

/**
 * Domänenmodell für einen Bibliotheksbenutzer.
 * Enthält fachliche Regeln für Ausleihberechtigung.
 * Im hexagonalen Modell ist das Domänenmodell frei von Framework-Abhängigkeiten.
 */
public class User {

    private static final int MAX_ACTIVE_LOANS = 3;

    private final UserId id;
    private final String name;
    private final String email;

    public User(UserId id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Prüft, ob der Benutzer ein weiteres Buch ausleihen darf.
     * Regeln: Maximal 3 aktive Ausleihen, keine überfälligen Ausleihen.
     */
    public boolean canBorrow(List<Loan> activeLoans) {
        if (activeLoans.size() >= MAX_ACTIVE_LOANS) {
            return false;
        }
        return !hasOverdueLoans(activeLoans);
    }

    /**
     * Prüft, ob der Benutzer überfällige Ausleihen hat.
     */
    public boolean hasOverdueLoans(List<Loan> activeLoans) {
        return activeLoans.stream().anyMatch(Loan::isOverdue);
    }

    public UserId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
