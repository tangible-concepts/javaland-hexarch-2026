package com.libraryflow.adapter.driven.loanadapter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA Repository für Loan-Entities.
 * Infrastruktur-Komponente innerhalb des Driven Adapters — nicht sichtbar für den Domänenkern.
 */
public interface LoanJpaRepository extends JpaRepository<LoanEntity, Long> {

    List<LoanEntity> findByUserIdAndReturnedFalse(Long userId);

    List<LoanEntity> findByBookIdAndReturnedFalse(Long bookId);

    List<LoanEntity> findByReturnedFalseAndDueDateBefore(LocalDate date);
}
