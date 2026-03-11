package com.libraryflow.domain.model;

import java.util.List;

// Rich Domain Model - Business-Logik im Objekt
public class User {

    private static final int MAX_LOANS = 3;

    private final UserId id;
    private final String name;
    private final String email;

    public User(UserId id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public boolean canBorrow(List<Loan> activeLoans) {
        if (activeLoans.size() >= MAX_LOANS) {
            return false;
        }
        return !hasOverdueLoans(activeLoans);
    }

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
