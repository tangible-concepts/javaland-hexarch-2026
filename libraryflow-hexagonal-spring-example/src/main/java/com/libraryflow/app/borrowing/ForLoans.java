package com.libraryflow.app.borrowing;

import com.libraryflow.app.catalog.Book;
import com.libraryflow.app.catalog.BookDetail;
import com.libraryflow.app.shared.*;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.LoanId;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.common.DrivingPort;

import java.util.List;

/**
 * Driving Port für alle Ausleih- und Suchfunktionen.
 *
 * In der hexagonalen Architektur definiert ein Driving Port die fachlichen Anwendungsfälle,
 * die von außen (z.B. REST-API) aufgerufen werden können. Die Implementierung liegt im
 * Domänenkern (Domain Service).
 *
 * @see <a href="docs/adr/004-custom-stereotype-annotations.md">ADR-004: Custom Stereotype-Annotationen</a>
 */
@DrivingPort
public interface ForLoans {

    Loan borrowBook(BorrowCommand command);

    Loan returnBook(LoanId loanId);

    List<Loan> findActiveLoansForUser(UserId userId);

    List<Book> searchBooks(String title);

    BookDetail findBookDetailById(BookId bookId);

    List<Book> findAvailableBooks();
}
