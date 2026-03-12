package com.libraryflow.domain.drivingport;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookDetail;
import com.libraryflow.domain.model.BookId;

import java.util.List;

public interface FindBook {

    Book findById(BookId bookId);

    BookDetail findBookDetailById(BookId bookId);

    List<Book> findAllBooks();

    List<Book> findAvailableBooks();

    List<Book> searchByTitle(String title);

    List<Book> searchBooks(String title, String author, Boolean available);
}
