package com.libraryflow.app.model;

/**
 * Value Object für die Identität eines Buches.
 * Im hexagonalen Modell gehören Value Objects zur Domäne und sind frei von Infrastruktur-Abhängigkeiten.
 */
public record BookId(Long value) {

    public BookId {
        if (value == null) {
            throw new IllegalArgumentException("BookId darf nicht null sein");
        }
    }
}
