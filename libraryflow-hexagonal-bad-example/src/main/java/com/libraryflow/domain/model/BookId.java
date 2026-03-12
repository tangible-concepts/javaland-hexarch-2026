package com.libraryflow.domain.model;

public record BookId(Long value) {

    public static BookId of(Long value) {
        return new BookId(value);
    }

    public static BookId of(String value) {
        return new BookId(Long.parseLong(value));
    }
}
