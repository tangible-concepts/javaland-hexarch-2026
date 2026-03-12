package com.libraryflow.adapter.driven.useradapter;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository für User-Entities.
 * Infrastruktur-Komponente innerhalb des Driven Adapters.
 */
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
