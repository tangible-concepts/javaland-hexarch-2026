package com.libraryflow.app.service;

import com.libraryflow.app.model.Book;
import com.libraryflow.app.model.ISBN;
import com.libraryflow.app.ports.driven.forcatalogmanagement.BookCatalog;

import java.util.List;

/**
 * Domain Service für die Katalogverwaltung.
 *
 * Enthält die fachliche Logik für Buchanlage und -abfrage.
 * Im hexagonalen Modell ist dieser Service frei von Framework-Annotationen.
 */
public class CatalogService {

    private final BookCatalog bookCatalog;

    public CatalogService(BookCatalog bookCatalog) {
        this.bookCatalog = bookCatalog;
    }

    public Book createBook(ISBN isbn, String title, String author) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titel darf nicht leer sein");
        }
        Book book = new Book(null, isbn, title, author, true);
        return bookCatalog.save(book);
    }

    public List<Book> findAllBooks() {
        return bookCatalog.findAll();
    }

    public List<Book> findAvailableBooks() {
        return bookCatalog.findAvailable();
    }
}
