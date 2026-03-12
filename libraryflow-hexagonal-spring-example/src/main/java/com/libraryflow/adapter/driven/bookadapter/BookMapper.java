package com.libraryflow.adapter.driven.bookadapter;

import com.libraryflow.app.catalog.Book;
import com.libraryflow.app.catalog.ISBN;
import com.libraryflow.app.shared.BookId;

/**
 * Mapper zwischen Domänenmodell {@link Book} und JPA-Entity {@link BookEntity}.
 *
 * Im hexagonalen Modell übersetzt der Mapper an der Grenze zwischen Domäne und Infrastruktur.
 */
class BookMapper {

    static Book toDomain(BookEntity entity) {
        return new Book(
                new BookId(entity.getId()),
                new ISBN(entity.getIsbn()),
                entity.getTitle(),
                entity.getAuthor(),
                entity.isAvailable()
        );
    }

    static BookEntity toEntity(Book book) {
        BookEntity entity = new BookEntity();
        if (book.getId() != null) {
            entity.setId(book.getId().value());
        }
        entity.setIsbn(book.getIsbn().value());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setAvailable(book.isAvailable());
        return entity;
    }
}
