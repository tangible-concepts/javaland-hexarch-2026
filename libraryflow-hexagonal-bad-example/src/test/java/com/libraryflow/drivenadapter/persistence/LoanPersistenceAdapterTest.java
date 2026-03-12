package com.libraryflow.drivenadapter.persistence;

import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(LoanPersistenceAdapter.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class LoanPersistenceAdapterTest {

    @Autowired
    private LoanPersistenceAdapter loanPersistenceAdapter;

    @Autowired
    private LoanJpaRepository loanJpaRepository;

    @Test
    void shouldSaveAndLoadLoan() {
        Loan loan = Loan.create(BookId.of(1L), UserId.of(1L));

        Loan saved = loanPersistenceAdapter.record(loan);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBookId()).isEqualTo(BookId.of(1L));
        assertThat(saved.getUserId()).isEqualTo(UserId.of(1L));
        assertThat(saved.isReturned()).isFalse();
    }

    @Test
    void shouldLoadLoanById() {
        LoanEntity entity = new LoanEntity(null, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), false);
        LoanEntity saved = loanJpaRepository.save(entity);

        Optional<Loan> result = loanPersistenceAdapter.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getBookId()).isEqualTo(BookId.of(1L));
    }

    @Test
    void shouldReturnEmptyWhenLoanNotFound() {
        Optional<Loan> result = loanPersistenceAdapter.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldLoadActiveByUserId() {
        loanJpaRepository.save(new LoanEntity(null, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), false));
        loanJpaRepository.save(new LoanEntity(null, 2L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), false));
        loanJpaRepository.save(new LoanEntity(null, 3L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), true));

        var result = loanPersistenceAdapter.findActiveByUserId(UserId.of(1L));

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldLoadActiveByBookId() {
        loanJpaRepository.save(new LoanEntity(null, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), false));
        loanJpaRepository.save(new LoanEntity(null, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(14), true));

        var result = loanPersistenceAdapter.findActiveByBookId(BookId.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(UserId.of(1L));
    }
}
