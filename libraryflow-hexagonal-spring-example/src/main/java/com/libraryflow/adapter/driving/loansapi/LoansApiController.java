package com.libraryflow.adapter.driving.loansapi;

import com.libraryflow.app.borrowing.BorrowCommand;
import com.libraryflow.app.borrowing.BorrowLimitExceededException;
import com.libraryflow.app.borrowing.ForLoans;
import com.libraryflow.app.borrowing.Loan;
import com.libraryflow.app.borrowing.LoanNotFoundException;
import com.libraryflow.app.catalog.BookDetail;
import com.libraryflow.app.catalog.BookNotAvailableException;
import com.libraryflow.app.catalog.BookNotFoundException;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.LoanId;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.UserNotFoundException;
import com.libraryflow.common.DrivingAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Driving Adapter für Ausleih- und Suchfunktionen.
 * <p>
 * In der hexagonalen Architektur ist der REST-Controller ein Driving Adapter,
 * der eingehende HTTP-Requests entgegennimmt und an den Driving Port {@link ForLoans} delegiert.
 * DTOs werden im Adapter in Domänenobjekte übersetzt und umgekehrt.
 */
@DrivingAdapter
@RestController
public class LoansApiController {

    private final ForLoans forLoans;

    public LoansApiController(ForLoans forLoans) {
        this.forLoans = forLoans;
    }

    @GetMapping("/api/books/{id}")
    public BookDetailResponse findBookDetailById(@PathVariable Long id) {
        try {
            BookDetail detail = forLoans.findBookDetailById(new BookId(id));
            return BookDetailResponse.from(detail);
        } catch (BookNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/api/books/search")
    public List<BookResponse> searchBooks(@RequestParam String title) {
        return forLoans.searchBooks(title).stream()
                .map(BookResponse::from).toList();
    }

    @GetMapping("/api/books/available")
    public List<BookResponse> findAvailableBooks() {
        return forLoans.findAvailableBooks().stream()
                .map(BookResponse::from).toList();
    }

    @PostMapping("/api/loans/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrowBook(@RequestBody BorrowRequest request) {
        try {
            Loan loan = forLoans.borrowBook(new BorrowCommand(
                    new BookId(request.bookId()), new UserId(request.userId())));
            return LoanResponse.from(loan);
        } catch (BookNotFoundException | UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (BookNotAvailableException | BorrowLimitExceededException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/api/loans/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id) {
        try {
            Loan loan = forLoans.returnBook(new LoanId(id));
            return LoanResponse.from(loan);
        } catch (LoanNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/api/loans/user/{userId}")
    public List<LoanResponse> findActiveLoansForUser(@PathVariable Long userId) {
        return forLoans.findActiveLoansForUser(new UserId(userId)).stream()
                .map(LoanResponse::from).toList();
    }
}
