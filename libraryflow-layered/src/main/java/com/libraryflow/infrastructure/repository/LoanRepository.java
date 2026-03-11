package com.libraryflow.infrastructure.repository;

import com.libraryflow.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserIdAndReturnedFalse(Long userId);

    List<Loan> findByBookIdAndReturnedFalse(Long bookId);

    List<Loan> findByReturnedFalseAndDueDateBefore(LocalDate date);
}
