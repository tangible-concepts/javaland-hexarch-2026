package com.libraryflow.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Rich Domain Model - Logik im Objekt
public class Loan {

    private static final int LOAN_PERIOD_DAYS = 14;

    private final Long id;
    private final BookId bookId;
    private final UserId userId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private boolean returned;

    public Loan(Long id, BookId bookId, UserId userId, LocalDate borrowDate, LocalDate dueDate, boolean returned) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    public static Loan create(BookId bookId, UserId userId) {
        return new Loan(null, bookId, userId, LocalDate.now(), LocalDate.now().plusDays(LOAN_PERIOD_DAYS), false);
    }

    public boolean isOverdue() {
        return !returned && dueDate.isBefore(LocalDate.now());
    }

    public long daysRemaining() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    public void markReturned() {
        this.returned = true;
    }

    public Long getId() {
        return id;
    }

    public BookId getBookId() {
        return bookId;
    }

    public UserId getUserId() {
        return userId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }
}
