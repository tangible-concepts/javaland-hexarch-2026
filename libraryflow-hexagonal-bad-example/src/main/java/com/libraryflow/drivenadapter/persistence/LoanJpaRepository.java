package com.libraryflow.drivenadapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanJpaRepository extends JpaRepository<LoanEntity, Long> {

    List<LoanEntity> findByUserIdAndReturnedFalse(Long userId);

    List<LoanEntity> findByBookIdAndReturnedFalse(Long bookId);
}
