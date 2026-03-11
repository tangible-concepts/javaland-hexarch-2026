package com.libraryflow.app.ports.driving.forloans;

import com.libraryflow.app.model.*;

import java.util.List;

/**
 * Driving Port für alle Ausleih- und Suchfunktionen.
 *
 * In der hexagonalen Architektur definiert ein Driving Port die fachlichen Anwendungsfälle,
 * die von außen (z.B. REST-API) aufgerufen werden können. Die Implementierung liegt im
 * Domänenkern (Domain Service).
 */
public interface ForLoans {

    Loan borrowBook(BorrowCommand command);

    Loan returnBook(Long loanId);

    List<Loan> findActiveLoansForUser(UserId userId);

    List<Book> searchBooks(String title);

    BookDetail findBookDetailById(BookId bookId);

    List<Book> findAvailableBooks();
}
