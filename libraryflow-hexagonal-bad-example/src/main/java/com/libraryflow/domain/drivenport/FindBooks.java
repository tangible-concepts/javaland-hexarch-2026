package com.libraryflow.domain.drivenport;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookId;

import java.util.List;
import java.util.Optional;

public interface FindBooks {

    Optional<Book> findById(BookId bookId);

    List<Book> findAll();

    List<Book> findAvailable();

    List<Book> findByTitleContaining(String title);
}
