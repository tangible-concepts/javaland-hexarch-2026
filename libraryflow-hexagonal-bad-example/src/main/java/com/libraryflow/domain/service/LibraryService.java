package com.libraryflow.domain.service;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookDetail;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.BookNotFoundException;
import com.libraryflow.domain.model.BorrowLimitExceededException;
import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserDetail;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.BorrowBookCommand;
import com.libraryflow.domain.drivingport.BorrowBook;
import com.libraryflow.domain.drivingport.FindBook;
import com.libraryflow.domain.drivingport.ManageUser;
import com.libraryflow.domain.drivingport.ReturnBook;
import com.libraryflow.domain.drivenport.FindBooks;
import com.libraryflow.domain.drivenport.FindLoans;
import com.libraryflow.domain.drivenport.FindUsers;
import com.libraryflow.domain.drivenport.UpdateBook;
import com.libraryflow.domain.drivenport.RecordLoan;
import com.libraryflow.domain.drivenport.RegisterUser;

import java.util.List;
import java.util.stream.Collectors;

// Pure Java - keine Spring-Annotationen!
// Constructor Injection, kein @Autowired
public class LibraryService implements BorrowBook, ReturnBook, FindBook, ManageUser {

    private final FindBooks findBooksPort;
    private final UpdateBook updateBookPort;
    private final FindUsers findUsersPort;
    private final RegisterUser registerUserPort;
    private final FindLoans findLoansPort;
    private final RecordLoan recordLoanPort;

    public LibraryService(FindBooks findBooksPort,
                          UpdateBook updateBookPort,
                          FindUsers findUsersPort,
                          RegisterUser registerUserPort,
                          FindLoans findLoansPort,
                          RecordLoan recordLoanPort) {
        this.findBooksPort = findBooksPort;
        this.updateBookPort = updateBookPort;
        this.findUsersPort = findUsersPort;
        this.registerUserPort = registerUserPort;
        this.findLoansPort = findLoansPort;
        this.recordLoanPort = recordLoanPort;
    }

    @Override
    public Loan borrowBook(BorrowBookCommand command) {
        Book book = findBooksPort.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));

        User user = findUsersPort.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("Nutzer mit ID " + command.userId().value() + " nicht gefunden"));

        List<Loan> activeLoans = findLoansPort.findActiveByUserId(command.userId());

        if (!user.canBorrow(activeLoans)) {
            throw new BorrowLimitExceededException(command.userId());
        }

        // Rich Domain Model - Logik im Domain-Objekt!
        book.borrowTo(command.userId());
        updateBookPort.update(book);

        Loan loan = Loan.create(command.bookId(), command.userId());
        return recordLoanPort.record(loan);
    }

    @Override
    public Loan returnBook(Long loanId) {
        Loan loan = findLoansPort.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Ausleihe mit ID " + loanId + " nicht gefunden"));

        Book book = findBooksPort.findById(loan.getBookId())
                .orElseThrow(() -> new BookNotFoundException(loan.getBookId()));

        loan.markReturned();
        book.returnBook();

        updateBookPort.update(book);
        return recordLoanPort.record(loan);
    }

    @Override
    public Book findById(BookId bookId) {
        return findBooksPort.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    @Override
    public BookDetail findBookDetailById(BookId bookId) {
        Book book = findById(bookId);

        if (book.isAvailable()) {
            return BookDetail.available(book);
        }

        List<Loan> activeLoans = findLoansPort.findActiveByBookId(bookId);
        if (activeLoans.isEmpty()) {
            return BookDetail.available(book);
        }

        Loan activeLoan = activeLoans.get(0);
        User borrower = findUsersPort.findById(activeLoan.getUserId())
                .orElseThrow(() -> new RuntimeException("Nutzer nicht gefunden"));

        return BookDetail.borrowed(book, borrower.getName(), activeLoan.getDueDate(), activeLoan.daysRemaining());
    }

    @Override
    public List<Book> findAllBooks() {
        return findBooksPort.findAll();
    }

    @Override
    public List<Book> findAvailableBooks() {
        return findBooksPort.findAvailable();
    }

    @Override
    public List<Book> searchByTitle(String title) {
        return findBooksPort.findByTitleContaining(title);
    }

    @Override
    public List<Book> searchBooks(String title, String author, Boolean available) {
        List<Book> results = findBooksPort.findByTitleContaining(title);

        if (author != null && !author.isBlank()) {
            results = results.stream()
                    .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (available != null) {
            results = results.stream()
                    .filter(book -> book.isAvailable() == available)
                    .collect(Collectors.toList());
        }

        return results;
    }

    @Override
    public User findById(UserId userId) {
        return findUsersPort.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nutzer mit ID " + userId.value() + " nicht gefunden"));
    }

    @Override
    public UserDetail findUserDetailById(UserId userId) {
        User user = findById(userId);
        List<Loan> activeLoans = findLoansPort.findActiveByUserId(userId);

        long overdueLoans = activeLoans.stream()
                .filter(Loan::isOverdue)
                .count();

        return UserDetail.from(user, activeLoans.size(), overdueLoans);
    }

    @Override
    public List<User> findAllUsers() {
        return findUsersPort.findAll();
    }

    @Override
    public User createUser(String name, String email) {
        User user = new User(null, name, email);
        return registerUserPort.register(user);
    }

    @Override
    public List<Loan> findActiveLoansForUser(UserId userId) {
        return findLoansPort.findActiveByUserId(userId);
    }
}
