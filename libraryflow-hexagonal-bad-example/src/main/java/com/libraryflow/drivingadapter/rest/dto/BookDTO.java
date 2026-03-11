package com.libraryflow.drivingadapter.rest.dto;

import com.libraryflow.domain.model.Book;

public record BookDTO(Long id, String isbn, String title, String author, boolean available) {

    public static BookDTO from(Book book) {
        return new BookDTO(
                book.getId().value(),
                book.getIsbn().value(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable()
        );
    }
}
