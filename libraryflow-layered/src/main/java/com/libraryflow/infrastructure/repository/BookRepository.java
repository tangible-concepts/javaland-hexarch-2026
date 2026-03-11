package com.libraryflow.infrastructure.repository;

import com.libraryflow.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAvailableTrue();

    List<Book> findByTitleContainingIgnoreCase(String title);
}
