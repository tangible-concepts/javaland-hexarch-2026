package com.libraryflow.drivenadapter.persistence;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.drivenport.FindBooks;
import com.libraryflow.domain.drivenport.UpdateBook;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookPersistenceAdapter implements FindBooks, UpdateBook {

    private final BookJpaRepository bookJpaRepository;
    private final BookMapper bookMapper;

    public BookPersistenceAdapter(BookJpaRepository bookJpaRepository, BookMapper bookMapper) {
        this.bookJpaRepository = bookJpaRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public Optional<Book> findById(BookId bookId) {
        return bookJpaRepository.findById(bookId.value())
                .map(bookMapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return bookJpaRepository.findAll().stream()
                .map(bookMapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findAvailable() {
        return bookJpaRepository.findByAvailableTrue().stream()
                .map(bookMapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        return bookJpaRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(bookMapper::toDomain)
                .toList();
    }

    @Override
    public Book update(Book book) {
        BookEntity entity = bookMapper.toEntity(book);
        BookEntity saved = bookJpaRepository.save(entity);
        return bookMapper.toDomain(saved);
    }
}
