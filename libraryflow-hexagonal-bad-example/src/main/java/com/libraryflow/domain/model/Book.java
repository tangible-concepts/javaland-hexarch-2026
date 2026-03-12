package com.libraryflow.domain.model;

// Rich Domain Model - POJO ohne Framework-Abhängigkeiten!
// Business-Logik lebt im Domain-Objekt
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

    // Business-Logik im Domain-Objekt!
    public void borrowTo(UserId userId) {
        if (!available) {
            throw new BookNotAvailableException(id);
        }
        this.available = false;
    }

    public void returnBook() {
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
