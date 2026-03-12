package com.libraryflow.app.borrowing;

import com.libraryflow.app.catalog.*;
import com.libraryflow.app.shared.*;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.LoanId;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Domain Service für Ausleih- und Suchfunktionen.
 *
 * Implementiert den Driving Port {@link ForLoans} und orchestriert die Geschäftslogik
 * unter Nutzung der Driven Ports (BookCatalog, LoanManagement, UserRepository).
 *
 * @see <a href="docs/adr/001-spring-stereotype-annotations.md">ADR-001: Spring Stereotype-Annotationen</a>
 * @see <a href="docs/adr/002-transactional-auf-services.md">ADR-002: Deklaratives Transaktionsmanagement</a>
 */
@Service
public class LoanService implements ForLoans {

    private final BookCatalog bookCatalog;
    private final LoanManagement loanManagement;
    private final UserRepository userRepository;

    public LoanService(BookCatalog bookCatalog, LoanManagement loanManagement, UserRepository userRepository) {
        this.bookCatalog = bookCatalog;
        this.loanManagement = loanManagement;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // ADR-002: Mehrere Schreiboperationen (Buch-Status + Loan) atomar ausführen
    public Loan borrowBook(BorrowCommand command) {
        Book book = bookCatalog.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));

        if (!book.isAvailable()) {
            throw new BookNotAvailableException(book.getTitle());
        }

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        List<Loan> activeLoans = loanManagement.findActiveByUserId(command.userId());
        boolean hasOverdue = activeLoans.stream().anyMatch(Loan::isOverdue);
        if (!user.canBorrow(activeLoans.size(), hasOverdue)) {
            if (activeLoans.size() >= 3) {
                throw new BorrowLimitExceededException(
                        "Nutzer '" + user.getName() + "' hat bereits 3 Bücher ausgeliehen");
            }
            throw new BorrowLimitExceededException(
                    "Nutzer '" + user.getName() + "' hat überfällige Ausleihen");
        }

        book.markAsBorrowed();
        bookCatalog.save(book);

        Loan loan = Loan.create(command.bookId(), command.userId());
        return loanManagement.save(loan);
    }

    @Override
    @Transactional // ADR-002: Mehrere Schreiboperationen (Loan-Status + Buch-Status) atomar ausführen
    public Loan returnBook(LoanId loanId) {
        Loan loan = loanManagement.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        loan.markReturned();

        Book book = bookCatalog.findById(loan.getBookId())
                .orElseThrow(() -> new BookNotFoundException(loan.getBookId()));
        book.returnBook();
        bookCatalog.save(book);

        return loanManagement.save(loan);
    }

    @Override
    public List<Loan> findActiveLoansForUser(UserId userId) {
        return loanManagement.findActiveByUserId(userId);
    }

    @Override
    public List<Book> searchBooks(String title) {
        return bookCatalog.findByTitleContaining(title);
    }

    @Override
    public BookDetail findBookDetailById(BookId bookId) {
        Book book = bookCatalog.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        String borrowedBy = null;
        java.time.LocalDate dueDate = null;
        long daysRemaining = 0;

        if (!book.isAvailable()) {
            List<Loan> activeLoans = loanManagement.findActiveByBookId(bookId);
            if (!activeLoans.isEmpty()) {
                Loan activeLoan = activeLoans.get(0);
                User borrower = userRepository.findById(activeLoan.getUserId()).orElse(null);
                borrowedBy = borrower != null ? borrower.getName() : null;
                dueDate = activeLoan.getDueDate();
                daysRemaining = activeLoan.daysRemaining();
            }
        }

        return new BookDetail(
                book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
                book.isAvailable(), borrowedBy, dueDate, daysRemaining
        );
    }

    @Override
    public List<Book> findAvailableBooks() {
        return bookCatalog.findAvailable();
    }
}
