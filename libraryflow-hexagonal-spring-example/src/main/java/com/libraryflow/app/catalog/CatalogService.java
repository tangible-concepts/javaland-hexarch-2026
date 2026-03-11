package com.libraryflow.app.catalog;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain Service für die Katalogverwaltung.
 *
 * Enthält die fachliche Logik für Buchanlage und -abfrage.
 *
 * @see <a href="docs/adr/001-spring-stereotype-annotations.md">ADR-001: Spring Stereotype-Annotationen</a>
 */
@Service // ADR-001
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
