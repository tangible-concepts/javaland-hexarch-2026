package com.libraryflow.app.shared;

/**
 * Value Object für die Identität eines Benutzers.
 * Im hexagonalen Modell gehören Value Objects zur Domäne und sind frei von Infrastruktur-Abhängigkeiten.
 */
public record UserId(Long value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId darf nicht null sein");
        }
    }
}
