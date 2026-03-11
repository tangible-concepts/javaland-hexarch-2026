package com.libraryflow.adapter.driving.loansapi;

import com.libraryflow.app.catalog.BookDetail;

import java.time.LocalDate;

/**
 * DTO für die Buchdetails über die REST-API, inklusive Ausleihinformationen.
 * Adapter-internes Objekt — entkoppelt die REST-Darstellung vom Domänenmodell.
 */
public record BookDetailResponse(
        Long id,
        String isbn,
        String title,
        String author,
        boolean available,
        String borrowedBy,
        LocalDate dueDate,
        long daysRemaining
) {
    static BookDetailResponse from(BookDetail detail) {
        return new BookDetailResponse(
                detail.id().value(),
                detail.isbn().value(),
                detail.title(),
                detail.author(),
                detail.available(),
                detail.borrowedBy(),
                detail.dueDate(),
                detail.daysRemaining()
        );
    }
}
