package com.libraryflow.app.users;

import com.libraryflow.app.shared.UserId;

/**
 * Fachliche Ausnahme: Benutzer wurde nicht gefunden.
 * Domänen-Exceptions sind Teil des hexagonalen Kerns und frei von Framework-Abhängigkeiten.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UserId userId) {
        super("Nutzer mit ID " + userId.value() + " nicht gefunden");
    }
}
