package com.libraryflow.adapter.driving.loansapi;

import com.libraryflow.app.borrowing.Loan;

import java.time.LocalDate;

/**
 * DTO für die Rückgabe einer Ausleihe über die REST-API.
 * Adapter-internes Objekt — entkoppelt die REST-Darstellung vom Domänenmodell.
 */
public record LoanResponse(
        Long id,
        Long bookId,
        Long userId,
        LocalDate borrowDate,
        LocalDate dueDate,
        boolean returned
) {
    static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId() != null ? loan.getId().value() : null,
                loan.getBookId().value(),
                loan.getUserId().value(),
                loan.getBorrowDate(),
                loan.getDueDate(),
                loan.isReturned()
        );
    }
}
