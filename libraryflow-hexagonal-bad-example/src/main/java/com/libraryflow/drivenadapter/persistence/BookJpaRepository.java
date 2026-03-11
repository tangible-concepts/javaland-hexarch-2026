package com.libraryflow.drivenadapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findByAvailableTrue();

    List<BookEntity> findByTitleContainingIgnoreCase(String title);
}
