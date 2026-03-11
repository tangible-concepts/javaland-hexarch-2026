package com.libraryflow.adapter.driven.loanadapter;

import com.libraryflow.app.model.BookId;
import com.libraryflow.app.model.Loan;
import com.libraryflow.app.model.UserId;
import com.libraryflow.app.ports.driven.forcatalogmanagement.LoanManagement;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Driven Adapter für die Ausleih-Persistenz.
 *
 * Implementiert den Driven Port {@link LoanManagement} und übersetzt zwischen
 * dem Domänenmodell und der JPA-Infrastruktur. In der hexagonalen Architektur
 * ist der Adapter die einzige Klasse, die sowohl den Port (Domäne) als auch
 * die JPA-Entities (Infrastruktur) kennt.
 */
@Component
public class LoanAdapter implements LoanManagement {

    private final LoanJpaRepository repository;

    public LoanAdapter(LoanJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return repository.findById(id).map(LoanMapper::toDomain);
    }

    @Override
    public List<Loan> findActiveByUserId(UserId userId) {
        return repository.findByUserIdAndReturnedFalse(userId.value())
                .stream().map(LoanMapper::toDomain).toList();
    }

    @Override
    public List<Loan> findActiveByBookId(BookId bookId) {
        return repository.findByBookIdAndReturnedFalse(bookId.value())
                .stream().map(LoanMapper::toDomain).toList();
    }

    @Override
    public List<Loan> findOverdue() {
        return repository.findByReturnedFalseAndDueDateBefore(LocalDate.now())
                .stream().map(LoanMapper::toDomain).toList();
    }

    @Override
    public Loan save(Loan loan) {
        LoanEntity entity = LoanMapper.toEntity(loan);
        LoanEntity saved = repository.save(entity);
        return LoanMapper.toDomain(saved);
    }
}
