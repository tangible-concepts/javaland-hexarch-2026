package com.libraryflow.drivingadapter.rest;

import com.libraryflow.drivingadapter.rest.dto.BorrowRequestDTO;
import com.libraryflow.drivingadapter.rest.dto.LoanDTO;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.BookNotAvailableException;
import com.libraryflow.domain.model.BookNotFoundException;
import com.libraryflow.domain.model.BorrowLimitExceededException;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.BorrowBookCommand;
import com.libraryflow.domain.drivingport.BorrowBook;
import com.libraryflow.domain.drivingport.ManageUser;
import com.libraryflow.domain.drivingport.ReturnBook;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final BorrowBook borrowBook;
    private final ReturnBook returnBook;
    private final ManageUser manageUser;

    public LoanController(BorrowBook borrowBook,
                           ReturnBook returnBook,
                           ManageUser manageUser) {
        this.borrowBook = borrowBook;
        this.returnBook = returnBook;
        this.manageUser = manageUser;
    }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO borrowBook(@RequestBody BorrowRequestDTO request) {
        var command = new BorrowBookCommand(BookId.of(request.bookId()), UserId.of(request.userId()));
        return LoanDTO.from(borrowBook.borrowBook(command));
    }

    @PostMapping("/{loanId}/return")
    public LoanDTO returnBook(@PathVariable Long loanId) {
        return LoanDTO.from(returnBook.returnBook(loanId));
    }

    @GetMapping("/user/{userId}")
    public List<LoanDTO> getActiveLoansForUser(@PathVariable Long userId) {
        return manageUser.findActiveLoansForUser(UserId.of(userId)).stream()
                .map(LoanDTO::from)
                .toList();
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBookNotFound(BookNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BookNotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleBookNotAvailable(BookNotAvailableException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BorrowLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleBorrowLimitExceeded(BorrowLimitExceededException ex) {
        return ex.getMessage();
    }
}
