package com.libraryflow.drivingadapter.graphql;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookDetail;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.ISBN;
import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.BorrowBookCommand;
import com.libraryflow.domain.drivingport.BorrowBook;
import com.libraryflow.domain.drivingport.FindBook;
import com.libraryflow.domain.drivingport.ReturnBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@GraphQlTest(BookGraphQLController.class)
class BookGraphQLControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private FindBook findBook;

    @MockitoBean
    private BorrowBook borrowBook;

    @MockitoBean
    private ReturnBook returnBook;

    @Test
    void shouldReturnAllBooks() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBook.findAllBooks()).thenReturn(List.of(book));

        graphQlTester.document("{ allBooks { id title author available } }")
                .execute()
                .path("allBooks[0].title").entity(String.class).isEqualTo("Clean Architecture")
                .path("allBooks[0].available").entity(Boolean.class).isEqualTo(true);
    }

    @Test
    void shouldReturnBookDetailById() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);
        var detail = BookDetail.borrowed(book, "Alice", LocalDate.now().plusDays(7), 7);
        when(findBook.findBookDetailById(BookId.of("1"))).thenReturn(detail);

        graphQlTester.document("{ book(id: \"1\") { title available borrowedBy daysRemaining } }")
                .execute()
                .path("book.title").entity(String.class).isEqualTo("Clean Architecture")
                .path("book.available").entity(Boolean.class).isEqualTo(false)
                .path("book.borrowedBy").entity(String.class).isEqualTo("Alice")
                .path("book.daysRemaining").entity(Integer.class).isEqualTo(7);
    }

    @Test
    void shouldReturnAvailableBooks() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBook.findAvailableBooks()).thenReturn(List.of(book));

        graphQlTester.document("{ availableBooks { title available } }")
                .execute()
                .path("availableBooks[0].available").entity(Boolean.class).isEqualTo(true);
    }

    @Test
    void shouldSearchBooks() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBook.searchBooks("Clean", null, null)).thenReturn(List.of(book));

        graphQlTester.document("{ searchBooks(title: \"Clean\") { title author } }")
                .execute()
                .path("searchBooks[0].title").entity(String.class).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldBorrowBook() {
        var loan = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now(), LocalDate.now().plusDays(14), false);
        when(borrowBook.borrowBook(any(BorrowBookCommand.class))).thenReturn(loan);

        graphQlTester.document("mutation { borrowBook(bookId: \"1\", userId: \"1\") { id bookId returned } }")
                .execute()
                .path("borrowBook.returned").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    void shouldReturnBookViaGraphQL() {
        var loan = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), true);
        when(returnBook.returnBook(1L)).thenReturn(loan);

        graphQlTester.document("mutation { returnBook(loanId: 1) { id returned } }")
                .execute()
                .path("returnBook.returned").entity(Boolean.class).isEqualTo(true);
    }
}
