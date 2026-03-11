package com.libraryflow.domain.model;

public record UserId(Long value) {

    public static UserId of(Long value) {
        return new UserId(value);
    }

    public static UserId of(String value) {
        return new UserId(Long.parseLong(value));
    }
}
