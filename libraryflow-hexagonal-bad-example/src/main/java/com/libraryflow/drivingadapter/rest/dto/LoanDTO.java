package com.libraryflow.drivingadapter.rest.dto;

import com.libraryflow.domain.model.Loan;

import java.time.LocalDate;

public record LoanDTO(Long id, Long bookId, Long userId, LocalDate borrowDate, LocalDate dueDate, boolean returned) {

    public static LoanDTO from(Loan loan) {
        return new LoanDTO(
                loan.getId(),
                loan.getBookId().value(),
                loan.getUserId().value(),
                loan.getBorrowDate(),
                loan.getDueDate(),
                loan.isReturned()
        );
    }
}
