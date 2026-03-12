package com.libraryflow.app.catalog;

import java.util.regex.Pattern;

/**
 * Value Object für eine ISBN (International Standard Book Number).
 * Validiert das ISBN-13-Format bei Erzeugung.
 * Im hexagonalen Modell gehören Value Objects zur Domäne und sind frei von Infrastruktur-Abhängigkeiten.
 */
public record ISBN(String value) {

    private static final Pattern ISBN_PATTERN = Pattern.compile("^978-\\d{1,5}-\\d{1,7}-\\d{1,7}-\\d$");

    public ISBN {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ISBN darf nicht leer sein");
        }
        if (!ISBN_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Ungültiges ISBN-Format: " + value);
        }
    }
}
