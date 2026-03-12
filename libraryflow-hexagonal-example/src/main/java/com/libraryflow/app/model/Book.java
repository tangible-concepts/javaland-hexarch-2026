package com.libraryflow.app.model;

/**
 * Domänenmodell für ein Buch in der Bibliothek.
 * Enthält fachliche Logik für Ausleihe und Rückgabe.
 * Im hexagonalen Modell ist das Domänenmodell frei von Framework-Abhängigkeiten (kein JPA, kein Spring).
 */
public class Book {

    private final BookId id;
    private final ISBN isbn;
    private final String title;
    private final String author;
    private boolean available;

    public Book(BookId id, ISBN isbn, String title, String author, boolean available) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    /**
     * Verleiht dieses Buch an einen Benutzer.
     * Setzt den Verfügbarkeitsstatus auf false.
     *
     * @throws IllegalStateException wenn das Buch bereits verliehen ist
     */
    public void borrowTo(UserId userId) {
        if (!available) {
            throw new IllegalStateException("Buch '" + title + "' ist nicht verfügbar");
        }
        this.available = false;
    }

    /**
     * Gibt dieses Buch zurück.
     * Setzt den Verfügbarkeitsstatus auf true.
     *
     * @throws IllegalStateException wenn das Buch bereits verfügbar ist
     */
    public void returnBook() {
        if (available) {
            throw new IllegalStateException("Buch '" + title + "' ist bereits verfügbar");
        }
        this.available = true;
    }

    public BookId getId() {
        return id;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }
}
