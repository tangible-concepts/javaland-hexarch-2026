package com.libraryflow.drivenadapter.persistence;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.ISBN;

import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toDomain(BookEntity entity) {
        return new Book(
                BookId.of(entity.getId()),
                ISBN.of(entity.getIsbn()),
                entity.getTitle(),
                entity.getAuthor(),
                entity.isAvailable()
        );
    }

    public BookEntity toEntity(Book book) {
        return new BookEntity(
                book.getId() != null ? book.getId().value() : null,
                book.getIsbn().value(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable()
        );
    }
}
