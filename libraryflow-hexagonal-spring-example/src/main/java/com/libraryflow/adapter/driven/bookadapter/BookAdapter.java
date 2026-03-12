package com.libraryflow.adapter.driven.bookadapter;

import com.libraryflow.app.catalog.Book;
import com.libraryflow.app.catalog.BookCatalog;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.common.DrivenAdapter;

import java.util.List;
import java.util.Optional;

/**
 * Driven Adapter für die Buch-Persistenz.
 *
 * Implementiert den Driven Port {@link BookCatalog} und übersetzt zwischen
 * dem Domänenmodell und der JPA-Infrastruktur.
 */
@DrivenAdapter
public class BookAdapter implements BookCatalog {

    private final BookJpaRepository repository;

    public BookAdapter(BookJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return repository.findById(id.value()).map(BookMapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream().map(BookMapper::toDomain).toList();
    }

    @Override
    public List<Book> findAvailable() {
        return repository.findByAvailableTrue().stream().map(BookMapper::toDomain).toList();
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        return repository.findByTitleContainingIgnoreCase(title).stream()
                .map(BookMapper::toDomain).toList();
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = BookMapper.toEntity(book);
        BookEntity saved = repository.save(entity);
        return BookMapper.toDomain(saved);
    }
}
