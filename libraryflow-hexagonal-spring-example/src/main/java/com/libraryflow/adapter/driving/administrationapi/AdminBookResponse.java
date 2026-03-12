package com.libraryflow.adapter.driving.administrationapi;

import com.libraryflow.app.catalog.Book;

/**
 * DTO für die Rückgabe eines Buches über die Admin-REST-API.
 */
public record AdminBookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        boolean available
) {
    static AdminBookResponse from(Book book) {
        return new AdminBookResponse(
                book.getId().value(),
                book.getIsbn().value(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable()
        );
    }
}
