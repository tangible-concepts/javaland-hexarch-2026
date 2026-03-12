package com.libraryflow.domain.model;

import java.time.LocalDate;

public record BookDetail(
        BookId id,
        ISBN isbn,
        String title,
        String author,
        boolean available,
        String borrowedBy,
        LocalDate dueDate,
        long daysRemaining
) {

    public static BookDetail available(Book book) {
        return new BookDetail(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), true, null, null, 0);
    }

    public static BookDetail borrowed(Book book, String borrowerName, LocalDate dueDate, long daysRemaining) {
        return new BookDetail(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), false, borrowerName, dueDate, daysRemaining);
    }
}
