package com.libraryflow.adapter.driven.bookadapter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA Repository für Book-Entities.
 * Infrastruktur-Komponente innerhalb des Driven Adapters.
 */
public interface BookJpaRepository extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findByAvailableTrue();

    List<BookEntity> findByTitleContainingIgnoreCase(String title);
}
