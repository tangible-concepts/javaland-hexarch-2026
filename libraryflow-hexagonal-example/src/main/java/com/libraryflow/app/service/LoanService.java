package com.libraryflow.app.service;

import com.libraryflow.app.model.*;
import com.libraryflow.app.ports.driven.forcatalogmanagement.BookCatalog;
import com.libraryflow.app.ports.driven.forcatalogmanagement.LoanManagement;
import com.libraryflow.app.ports.driven.forusermanagement.UserRepository;
import com.libraryflow.app.ports.driving.forloans.BorrowCommand;
import com.libraryflow.app.ports.driving.forloans.ForLoans;

import java.util.List;

/**
 * Domain Service für Ausleih- und Suchfunktionen.
 *
 * Implementiert den Driving Port {@link ForLoans} und orchestriert die Geschäftslogik
 * unter Nutzung der Driven Ports (BookCatalog, LoanManagement, UserRepository).
 * Im hexagonalen Modell enthält der Domain Service keine Framework-Annotationen —
 * das Wiring erfolgt über die BeanConfiguration.
 */
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
    public Loan borrowBook(BorrowCommand command) {
        Book book = bookCatalog.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));

        if (!book.isAvailable()) {
            throw new BookNotAvailableException(book.getTitle());
        }

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        List<Loan> activeLoans = loanManagement.findActiveByUserId(command.userId());
        if (!user.canBorrow(activeLoans)) {
            if (activeLoans.size() >= 3) {
                throw new BorrowLimitExceededException(
                        "Nutzer '" + user.getName() + "' hat bereits 3 Bücher ausgeliehen");
            }
            throw new BorrowLimitExceededException(
                    "Nutzer '" + user.getName() + "' hat überfällige Ausleihen");
        }

        book.borrowTo(command.userId());
        bookCatalog.save(book);

        Loan loan = Loan.create(command.bookId(), command.userId());
        return loanManagement.save(loan);
    }

    @Override
    public Loan returnBook(Long loanId) {
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
