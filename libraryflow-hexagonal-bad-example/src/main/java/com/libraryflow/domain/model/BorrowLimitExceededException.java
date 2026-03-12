package com.libraryflow.domain.model;

public class BorrowLimitExceededException extends RuntimeException {

    public BorrowLimitExceededException(UserId userId) {
        super("Nutzer mit ID " + userId.value() + " hat das Ausleihlimit erreicht");
    }
}
