package com.libraryflow.service;

import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import com.libraryflow.infrastructure.repository.BookRepository;
import com.libraryflow.infrastructure.repository.LoanRepository;
import com.libraryflow.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Loan borrowBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Buch mit ID " + bookId + " nicht gefunden"));

        if (!book.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Buch '" + book.getTitle() + "' ist nicht verfügbar");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nutzer mit ID " + userId + " nicht gefunden"));

        List<Loan> activeLoans = loanRepository.findByUserIdAndReturnedFalse(userId);
        if (activeLoans.size() >= 3) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Nutzer '" + user.getName() + "' hat bereits 3 Bücher ausgeliehen");
        }

        boolean hasOverdue = activeLoans.stream()
                .anyMatch(loan -> loan.getDueDate().isBefore(LocalDate.now()));
        if (hasOverdue) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Nutzer '" + user.getName() + "' hat überfällige Ausleihen");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setReturned(false);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ausleihe mit ID " + loanId + " nicht gefunden"));

        if (loan.isReturned()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Buch wurde bereits zurückgegeben");
        }

        loan.setReturned(true);

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public List<Loan> getActiveLoansForUser(Long userId) {
        return loanRepository.findByUserIdAndReturnedFalse(userId);
    }

    public List<Loan> getActiveLoansForBook(Long bookId) {
        return loanRepository.findByBookIdAndReturnedFalse(bookId);
    }
}
