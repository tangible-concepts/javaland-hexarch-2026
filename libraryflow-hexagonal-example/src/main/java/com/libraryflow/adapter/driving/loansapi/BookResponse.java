package com.libraryflow.adapter.driving.loansapi;

import com.libraryflow.app.model.Book;

/**
 * DTO für die Rückgabe eines Buches über die Loans-REST-API.
 * Adapter-internes Objekt — entkoppelt die REST-Darstellung vom Domänenmodell.
 */
public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        boolean available
) {
    static BookResponse from(Book book) {
        return new BookResponse(
                book.getId().value(),
                book.getIsbn().value(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable()
        );
    }
}
