package com.libraryflow.adapter.driven.bookadapter;

import jakarta.persistence.*;

/**
 * JPA-Entity für Bücher.
 *
 * Im hexagonalen Modell ist die JPA-Entity Teil des Driven Adapters.
 * Das eigene Schema BOOKS sorgt für Isolation der Datenhaltung.
 */
@Entity
@Table(name = "BOOK", schema = "BOOKS")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;
    private String title;
    private String author;
    private boolean available;

    public BookEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
