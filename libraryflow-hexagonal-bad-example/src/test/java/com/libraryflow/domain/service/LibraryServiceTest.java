package com.libraryflow.domain.service;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookDetail;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.BookNotAvailableException;
import com.libraryflow.domain.model.BookNotFoundException;
import com.libraryflow.domain.model.BorrowLimitExceededException;
import com.libraryflow.domain.model.ISBN;
import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserDetail;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.BorrowBookCommand;
import com.libraryflow.domain.drivenport.FindBooks;
import com.libraryflow.domain.drivenport.FindLoans;
import com.libraryflow.domain.drivenport.FindUsers;
import com.libraryflow.domain.drivenport.UpdateBook;
import com.libraryflow.domain.drivenport.RecordLoan;
import com.libraryflow.domain.drivenport.RegisterUser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Plain Java Test - kein Spring, kein @SpringBootTest, blitzschnell!
class LibraryServiceTest {

    private final FindBooks findBooksPort = mock(FindBooks.class);
    private final UpdateBook updateBookPort = mock(UpdateBook.class);
    private final FindUsers findUsersPort = mock(FindUsers.class);
    private final RegisterUser registerUserPort = mock(RegisterUser.class);
    private final FindLoans findLoansPort = mock(FindLoans.class);
    private final RecordLoan recordLoanPort = mock(RecordLoan.class);

    private final LibraryService libraryService = new LibraryService(
            findBooksPort, updateBookPort, findUsersPort, registerUserPort, findLoansPort, recordLoanPort
    );

    // --- borrowBook ---

    @Test
    void shouldBorrowAvailableBook() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        var user = new User(UserId.of(1L), "Alice", "alice@example.com");
        var command = new BorrowBookCommand(BookId.of(1L), UserId.of(1L));

        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));
        when(findUsersPort.findById(UserId.of(1L))).thenReturn(Optional.of(user));
        when(findLoansPort.findActiveByUserId(UserId.of(1L))).thenReturn(List.of());
        when(recordLoanPort.record(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = libraryService.borrowBook(command);

        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(BookId.of(1L));
        assertThat(result.getUserId()).isEqualTo(UserId.of(1L));
        verify(updateBookPort).update(any(Book.class));
        verify(recordLoanPort).record(any(Loan.class));
    }

    @Test
    void shouldRejectBorrowWhenBookNotFound() {
        var command = new BorrowBookCommand(BookId.of(99L), UserId.of(1L));
        when(findBooksPort.findById(BookId.of(99L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.borrowBook(command))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void shouldRejectBorrowWhenBookNotAvailable() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);
        var user = new User(UserId.of(1L), "Alice", "alice@example.com");
        var command = new BorrowBookCommand(BookId.of(1L), UserId.of(1L));

        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));
        when(findUsersPort.findById(UserId.of(1L))).thenReturn(Optional.of(user));
        when(findLoansPort.findActiveByUserId(UserId.of(1L))).thenReturn(List.of());

        assertThatThrownBy(() -> libraryService.borrowBook(command))
                .isInstanceOf(BookNotAvailableException.class);
    }

    @Test
    void shouldRejectBorrowWhenLimitReached() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        var user = new User(UserId.of(1L), "Alice", "alice@example.com");
        var command = new BorrowBookCommand(BookId.of(1L), UserId.of(1L));

        var loan1 = new Loan(1L, BookId.of(2L), UserId.of(1L), LocalDate.now(), LocalDate.now().plusDays(14), false);
        var loan2 = new Loan(2L, BookId.of(3L), UserId.of(1L), LocalDate.now(), LocalDate.now().plusDays(14), false);
        var loan3 = new Loan(3L, BookId.of(4L), UserId.of(1L), LocalDate.now(), LocalDate.now().plusDays(14), false);

        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));
        when(findUsersPort.findById(UserId.of(1L))).thenReturn(Optional.of(user));
        when(findLoansPort.findActiveByUserId(UserId.of(1L))).thenReturn(List.of(loan1, loan2, loan3));

        assertThatThrownBy(() -> libraryService.borrowBook(command))
                .isInstanceOf(BorrowLimitExceededException.class);
    }

    @Test
    void shouldRejectBorrowWhenUserNotFound() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        var command = new BorrowBookCommand(BookId.of(1L), UserId.of(99L));

        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));
        when(findUsersPort.findById(UserId.of(99L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.borrowBook(command))
                .isInstanceOf(RuntimeException.class);
    }

    // --- returnBook ---

    @Test
    void shouldReturnBook() {
        var loan = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), false);
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);

        when(findLoansPort.findById(1L)).thenReturn(Optional.of(loan));
        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));
        when(recordLoanPort.record(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = libraryService.returnBook(1L);

        assertThat(result.isReturned()).isTrue();
        verify(updateBookPort).update(any(Book.class));
    }

    @Test
    void shouldRejectReturnWhenLoanNotFound() {
        when(findLoansPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.returnBook(99L))
                .isInstanceOf(RuntimeException.class);
    }

    // --- findAllBooks ---

    @Test
    void shouldFindAllBooks() {
        var book1 = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        var book2 = new Book(BookId.of(2L), ISBN.of("978-0-13-235088-4"), "Clean Code", "Robert C. Martin", true);

        when(findBooksPort.findAll()).thenReturn(List.of(book1, book2));

        List<Book> result = libraryService.findAllBooks();

        assertThat(result).hasSize(2);
    }

    // --- findById(BookId) ---

    @Test
    void shouldFindBookById() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));

        Book result = libraryService.findById(BookId.of(1L));

        assertThat(result.getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        when(findBooksPort.findById(BookId.of(99L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.findById(BookId.of(99L)))
                .isInstanceOf(BookNotFoundException.class);
    }

    // --- findBookDetailById ---

    @Test
    void shouldReturnAvailableBookDetail() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));

        BookDetail detail = libraryService.findBookDetailById(BookId.of(1L));

        assertThat(detail.available()).isTrue();
        assertThat(detail.borrowedBy()).isNull();
    }

    @Test
    void shouldReturnBorrowedBookDetailWithLoanInfo() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);
        var user = new User(UserId.of(2L), "Alice", "alice@example.com");
        var loan = new Loan(1L, BookId.of(1L), UserId.of(2L), LocalDate.now().minusDays(3), LocalDate.now().plusDays(11), false);

        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));
        when(findLoansPort.findActiveByBookId(BookId.of(1L))).thenReturn(List.of(loan));
        when(findUsersPort.findById(UserId.of(2L))).thenReturn(Optional.of(user));

        BookDetail detail = libraryService.findBookDetailById(BookId.of(1L));

        assertThat(detail.available()).isFalse();
        assertThat(detail.borrowedBy()).isEqualTo("Alice");
        assertThat(detail.dueDate()).isEqualTo(LocalDate.now().plusDays(11));
    }

    @Test
    void shouldReturnAvailableDetailWhenNoActiveLoans() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", false);

        when(findBooksPort.findById(BookId.of(1L))).thenReturn(Optional.of(book));
        when(findLoansPort.findActiveByBookId(BookId.of(1L))).thenReturn(List.of());

        BookDetail detail = libraryService.findBookDetailById(BookId.of(1L));

        assertThat(detail.borrowedBy()).isNull();
    }

    // --- findAvailableBooks ---

    @Test
    void shouldFindAvailableBooks() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBooksPort.findAvailable()).thenReturn(List.of(book));

        List<Book> result = libraryService.findAvailableBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isTrue();
    }

    // --- searchByTitle ---

    @Test
    void shouldSearchByTitle() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBooksPort.findByTitleContaining("Clean")).thenReturn(List.of(book));

        List<Book> result = libraryService.searchByTitle("Clean");

        assertThat(result).hasSize(1);
    }

    // --- searchBooks ---

    @Test
    void shouldSearchBooksWithAllFilters() {
        var book1 = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        var book2 = new Book(BookId.of(2L), ISBN.of("978-0-13-235088-4"), "Clean Code", "Robert C. Martin", false);
        var book3 = new Book(BookId.of(3L), ISBN.of("978-0-13-708107-3"), "Clean Coder", "Other Author", true);

        when(findBooksPort.findByTitleContaining("Clean")).thenReturn(List.of(book1, book2, book3));

        List<Book> result = libraryService.searchBooks("Clean", "Martin", true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldSearchBooksWithoutFilters() {
        var book = new Book(BookId.of(1L), ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        when(findBooksPort.findByTitleContaining("Clean")).thenReturn(List.of(book));

        List<Book> result = libraryService.searchBooks("Clean", null, null);

        assertThat(result).hasSize(1);
    }

    // --- findById(UserId) ---

    @Test
    void shouldFindUserById() {
        var user = new User(UserId.of(1L), "Alice", "alice@example.com");
        when(findUsersPort.findById(UserId.of(1L))).thenReturn(Optional.of(user));

        User result = libraryService.findById(UserId.of(1L));

        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(findUsersPort.findById(UserId.of(99L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.findById(UserId.of(99L)))
                .isInstanceOf(RuntimeException.class);
    }

    // --- findUserDetailById ---

    @Test
    void shouldReturnUserDetailWithLoanStats() {
        var user = new User(UserId.of(1L), "Alice", "alice@example.com");
        var loan1 = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), false);
        var loan2 = new Loan(2L, BookId.of(2L), UserId.of(1L), LocalDate.now().minusDays(3), LocalDate.now().plusDays(11), false);

        when(findUsersPort.findById(UserId.of(1L))).thenReturn(Optional.of(user));
        when(findLoansPort.findActiveByUserId(UserId.of(1L))).thenReturn(List.of(loan1, loan2));

        UserDetail detail = libraryService.findUserDetailById(UserId.of(1L));

        assertThat(detail.name()).isEqualTo("Alice");
        assertThat(detail.activeLoans()).isEqualTo(2);
        assertThat(detail.overdueLoans()).isEqualTo(1);
        assertThat(detail.remainingBorrowSlots()).isEqualTo(1);
    }

    // --- findAllUsers ---

    @Test
    void shouldFindAllUsers() {
        var user1 = new User(UserId.of(1L), "Alice", "alice@example.com");
        var user2 = new User(UserId.of(2L), "Bob", "bob@example.com");
        when(findUsersPort.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = libraryService.findAllUsers();

        assertThat(result).hasSize(2);
    }

    // --- createUser ---

    @Test
    void shouldCreateUser() {
        when(registerUserPort.register(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return new User(UserId.of(1L), u.getName(), u.getEmail());
        });

        User result = libraryService.createUser("Alice", "alice@example.com");

        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getId()).isEqualTo(UserId.of(1L));
    }

    // --- findActiveLoansForUser ---

    @Test
    void shouldFindActiveLoansForUser() {
        var loan = new Loan(1L, BookId.of(1L), UserId.of(1L), LocalDate.now(), LocalDate.now().plusDays(14), false);
        when(findLoansPort.findActiveByUserId(UserId.of(1L))).thenReturn(List.of(loan));

        List<Loan> result = libraryService.findActiveLoansForUser(UserId.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookId()).isEqualTo(BookId.of(1L));
    }
}
