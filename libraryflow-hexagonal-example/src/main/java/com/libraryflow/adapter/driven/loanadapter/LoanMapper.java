package com.libraryflow.adapter.driven.loanadapter;

import com.libraryflow.app.model.BookId;
import com.libraryflow.app.model.Loan;
import com.libraryflow.app.model.UserId;

/**
 * Mapper zwischen Domänenmodell {@link Loan} und JPA-Entity {@link LoanEntity}.
 *
 * Im hexagonalen Modell übersetzt der Mapper an der Grenze zwischen Domäne und Infrastruktur.
 * Die Domäne bleibt so frei von JPA-Abhängigkeiten.
 */
class LoanMapper {

    static Loan toDomain(LoanEntity entity) {
        return Loan.reconstitute(
                entity.getId(),
                new BookId(entity.getBookId()),
                new UserId(entity.getUserId()),
                entity.getBorrowDate(),
                entity.getDueDate(),
                entity.isReturned()
        );
    }

    static LoanEntity toEntity(Loan loan) {
        LoanEntity entity = new LoanEntity();
        entity.setId(loan.getId());
        entity.setBookId(loan.getBookId().value());
        entity.setUserId(loan.getUserId().value());
        entity.setBorrowDate(loan.getBorrowDate());
        entity.setDueDate(loan.getDueDate());
        entity.setReturned(loan.isReturned());
        return entity;
    }
}
