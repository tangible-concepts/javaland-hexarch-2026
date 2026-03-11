package com.libraryflow.domain.model;

public record ISBN(String value) {

    public ISBN {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ISBN darf nicht leer sein");
        }
        if (!value.matches("^978-\\d{1,5}-\\d{1,7}-\\d{1,7}-\\d$")) {
            throw new IllegalArgumentException("Ungültiges ISBN-Format: " + value);
        }
    }

    public static ISBN of(String value) {
        return new ISBN(value);
    }
}
