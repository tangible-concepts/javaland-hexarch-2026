package com.libraryflow.drivingadapter.rest.dto;

import com.libraryflow.domain.model.BookDetail;

import java.time.LocalDate;

public record BookDetailDTO(
        Long id,
        String isbn,
        String title,
        String author,
        boolean available,
        String borrowedBy,
        LocalDate dueDate,
        long daysRemaining
) {

    public static BookDetailDTO from(BookDetail detail) {
        return new BookDetailDTO(
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
