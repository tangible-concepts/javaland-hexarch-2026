package com.libraryflow.app.ports.driven.forcatalogmanagement;

import com.libraryflow.app.model.Book;
import com.libraryflow.app.model.BookId;

import java.util.List;
import java.util.Optional;

/**
 * Driven Port für den Zugriff auf den Buchkatalog.
 *
 * In der hexagonalen Architektur definiert ein Driven Port die Schnittstelle,
 * die der Domänenkern von der Infrastruktur benötigt. Die Implementierung
 * erfolgt durch einen Driven Adapter (z.B. JPA-basiert).
 */
public interface BookCatalog {

    Optional<Book> findById(BookId id);

    List<Book> findAll();

    List<Book> findAvailable();

    List<Book> findByTitleContaining(String title);

    Book save(Book book);
}
