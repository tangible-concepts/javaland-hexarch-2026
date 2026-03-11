package com.libraryflow.adapter.driven.loanadapter;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * JPA-Entity für Ausleihen.
 *
 * Im hexagonalen Modell ist die JPA-Entity Teil des Driven Adapters und dient ausschließlich
 * der Persistenz. Sie wird über den {@link LoanMapper} in das Domänenmodell übersetzt.
 * Das eigene Schema LOANS sorgt für Isolation der Datenhaltung.
 */
@Entity
@Table(name = "LOAN", schema = "LOANS")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;
    private Long userId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;

    public LoanEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
