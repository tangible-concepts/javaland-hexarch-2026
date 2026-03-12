package com.libraryflow.drivenadapter.persistence;

import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivenport.FindLoans;
import com.libraryflow.domain.drivenport.RecordLoan;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LoanPersistenceAdapter implements FindLoans, RecordLoan {

    private final LoanJpaRepository loanJpaRepository;

    public LoanPersistenceAdapter(LoanJpaRepository loanJpaRepository) {
        this.loanJpaRepository = loanJpaRepository;
    }

    @Override
    public Optional<Loan> findById(Long loanId) {
        return loanJpaRepository.findById(loanId)
                .map(this::toDomain);
    }

    @Override
    public List<Loan> findActiveByUserId(UserId userId) {
        return loanJpaRepository.findByUserIdAndReturnedFalse(userId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findActiveByBookId(BookId bookId) {
        return loanJpaRepository.findByBookIdAndReturnedFalse(bookId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Loan record(Loan loan) {
        LoanEntity entity = toEntity(loan);
        LoanEntity saved = loanJpaRepository.save(entity);
        return toDomain(saved);
    }

    private Loan toDomain(LoanEntity entity) {
        return new Loan(
                entity.getId(),
                BookId.of(entity.getBookId()),
                UserId.of(entity.getUserId()),
                entity.getBorrowDate(),
                entity.getDueDate(),
                entity.isReturned()
        );
    }

    private LoanEntity toEntity(Loan loan) {
        return new LoanEntity(
                loan.getId(),
                loan.getBookId().value(),
                loan.getUserId().value(),
                loan.getBorrowDate(),
                loan.getDueDate(),
                loan.isReturned()
        );
    }
}
