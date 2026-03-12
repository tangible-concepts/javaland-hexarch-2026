package com.libraryflow.drivingadapter.graphql;

import com.libraryflow.drivingadapter.rest.dto.BookDTO;
import com.libraryflow.drivingadapter.rest.dto.BookDetailDTO;
import com.libraryflow.drivingadapter.rest.dto.LoanDTO;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.BorrowBookCommand;
import com.libraryflow.domain.drivingport.BorrowBook;
import com.libraryflow.domain.drivingport.FindBook;
import com.libraryflow.domain.drivingport.ReturnBook;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

// Demonstriert Flexibilität: Neuer Adapter OHNE Domain-Änderung!
// Gleiche Use Cases wie REST, nur andere Schnittstelle
@Controller
public class BookGraphQLController {

    private final FindBook findBook;
    private final BorrowBook borrowBook;
    private final ReturnBook returnBook;

    public BookGraphQLController(FindBook findBook,
                                  BorrowBook borrowBook,
                                  ReturnBook returnBook) {
        this.findBook = findBook;
        this.borrowBook = borrowBook;
        this.returnBook = returnBook;
    }

    @QueryMapping
    public BookDetailDTO book(@Argument String id) {
        return BookDetailDTO.from(findBook.findBookDetailById(BookId.of(id)));
    }

    @QueryMapping
    public List<BookDTO> allBooks() {
        return findBook.findAllBooks().stream()
                .map(BookDTO::from)
                .toList();
    }

    @QueryMapping
    public List<BookDTO> availableBooks() {
        return findBook.findAvailableBooks().stream()
                .map(BookDTO::from)
                .toList();
    }

    @QueryMapping
    public List<BookDTO> searchBooks(@Argument String title, @Argument String author, @Argument Boolean available) {
        return findBook.searchBooks(title, author, available).stream()
                .map(BookDTO::from)
                .toList();
    }

    @MutationMapping
    public LoanDTO borrowBook(@Argument String bookId, @Argument String userId) {
        var command = new BorrowBookCommand(BookId.of(bookId), UserId.of(userId));
        return LoanDTO.from(borrowBook.borrowBook(command));
    }

    @MutationMapping
    public LoanDTO returnBook(@Argument Long loanId) {
        return LoanDTO.from(returnBook.returnBook(loanId));
    }
}
