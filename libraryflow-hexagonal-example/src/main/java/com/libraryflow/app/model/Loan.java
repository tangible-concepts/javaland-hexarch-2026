package com.libraryflow.app.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Domänenmodell für eine Buchausleihe.
 * Enthält fachliche Logik für Fristberechnung und Rückgabe.
 * Im hexagonalen Modell ist das Domänenmodell frei von Framework-Abhängigkeiten.
 */
public class Loan {

    private static final int LOAN_DURATION_DAYS = 14;

    private final Long id;
    private final BookId bookId;
    private final UserId userId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private boolean returned;

    private Loan(Long id, BookId bookId, UserId userId, LocalDate borrowDate, LocalDate dueDate, boolean returned) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    /**
     * Erzeugt eine neue Ausleihe mit dem heutigen Datum und einer Leihfrist von 14 Tagen.
     */
    public static Loan create(BookId bookId, UserId userId) {
        LocalDate now = LocalDate.now();
        return new Loan(null, bookId, userId, now, now.plusDays(LOAN_DURATION_DAYS), false);
    }

    /**
     * Rekonstruiert eine bestehende Ausleihe (z.B. aus der Datenbank).
     */
    public static Loan reconstitute(Long id, BookId bookId, UserId userId,
                                     LocalDate borrowDate, LocalDate dueDate, boolean returned) {
        return new Loan(id, bookId, userId, borrowDate, dueDate, returned);
    }

    /**
     * Prüft, ob die Leihfrist überschritten ist.
     */
    public boolean isOverdue() {
        return !returned && dueDate.isBefore(LocalDate.now());
    }

    /**
     * Berechnet die verbleibenden Tage bis zur Rückgabe.
     * Negative Werte bedeuten Überschreitung.
     */
    public long daysRemaining() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Markiert die Ausleihe als zurückgegeben.
     *
     * @throws IllegalStateException wenn die Ausleihe bereits zurückgegeben wurde
     */
    public void markReturned() {
        if (returned) {
            throw new IllegalStateException("Buch wurde bereits zurückgegeben");
        }
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
