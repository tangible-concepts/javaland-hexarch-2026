package com.libraryflow.adapter.driven.loanadapter;

import com.libraryflow.app.borrowing.Loan;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.LoanId;
import com.libraryflow.app.shared.UserId;

/**
 * Mapper zwischen Domänenmodell {@link Loan} und JPA-Entity {@link LoanEntity}.
 *
 * Im hexagonalen Modell übersetzt der Mapper an der Grenze zwischen Domäne und Infrastruktur.
 * Die Domäne bleibt so frei von JPA-Abhängigkeiten.
 */
class LoanMapper {

    static Loan toDomain(LoanEntity entity) {
        return Loan.reconstitute(
                new LoanId(entity.getId()),
                new BookId(entity.getBookId()),
                new UserId(entity.getUserId()),
                entity.getBorrowDate(),
                entity.getDueDate(),
                entity.isReturned()
        );
    }

    static LoanEntity toEntity(Loan loan) {
        LoanEntity entity = new LoanEntity();
        entity.setId(loan.getId() != null ? loan.getId().value() : null);
        entity.setBookId(loan.getBookId().value());
        entity.setUserId(loan.getUserId().value());
        entity.setBorrowDate(loan.getBorrowDate());
        entity.setDueDate(loan.getDueDate());
        entity.setReturned(loan.isReturned());
        return entity;
    }
}
