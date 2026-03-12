package com.libraryflow.adapter.driven.loanadapter;

import com.libraryflow.app.model.BookId;
import com.libraryflow.app.model.Loan;
import com.libraryflow.app.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(LoanAdapter.class)
class LoanAdapterTest {

    @Autowired
    private LoanAdapter loanAdapter;

    @Test
    void save_andFindById_roundTrip() {
        Loan loan = Loan.create(new BookId(1L), new UserId(1L));
        Loan saved = loanAdapter.save(loan);

        assertNotNull(saved.getId());

        Optional<Loan> found = loanAdapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findActiveByUserId_returnsOnlyActiveLoans() {
        UserId userId = new UserId(10L);
        loanAdapter.save(Loan.create(new BookId(1L), userId));
        loanAdapter.save(Loan.create(new BookId(2L), userId));

        Loan returnedLoan = Loan.create(new BookId(3L), userId);
        Loan savedReturned = loanAdapter.save(returnedLoan);
        Loan reconstituted = Loan.reconstitute(savedReturned.getId(), new BookId(3L), userId,
                savedReturned.getBorrowDate(), savedReturned.getDueDate(), true);
        loanAdapter.save(reconstituted);

        List<Loan> active = loanAdapter.findActiveByUserId(userId);
        assertEquals(2, active.size());
    }

    @Test
    void findActiveByBookId_returnsActiveLoansForBook() {
        BookId bookId = new BookId(20L);
        loanAdapter.save(Loan.create(bookId, new UserId(1L)));

        List<Loan> active = loanAdapter.findActiveByBookId(bookId);
        assertEquals(1, active.size());
    }

    @Test
    void findOverdue_returnsOnlyOverdueLoans() {
        Loan overdue = Loan.reconstitute(null, new BookId(30L), new UserId(1L),
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(1), false);
        loanAdapter.save(overdue);

        Loan notOverdue = Loan.create(new BookId(31L), new UserId(1L));
        loanAdapter.save(notOverdue);

        List<Loan> overdueLoans = loanAdapter.findOverdue();
        assertEquals(1, overdueLoans.size());
    }
}
