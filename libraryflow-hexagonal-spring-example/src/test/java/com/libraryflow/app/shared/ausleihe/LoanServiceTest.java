package com.libraryflow.app.shared.ausleihe;

import com.libraryflow.app.borrowing.*;
import com.libraryflow.app.catalog.*;
import com.libraryflow.app.shared.*;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.LoanId;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private BookCatalog bookCatalog;
    @Mock
    private LoanManagement loanManagement;
    @Mock
    private UserRepository userRepository;

    private LoanService loanService;

    private static final BookId BOOK_ID = new BookId(1L);
    private static final UserId USER_ID = new UserId(1L);
    private static final LoanId LOAN_ID = new LoanId(1L);
    private static final ISBN TEST_ISBN = new ISBN("978-0-13-468599-1");

    @BeforeEach
    void setUp() {
        loanService = new LoanService(bookCatalog, loanManagement, userRepository);
    }

    // --- borrowBook Tests ---

    @Test
    void borrowBook_happyPath_createsLoan() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", true);
        User user = new User(USER_ID, "Alice", "alice@example.com");
        Loan savedLoan = Loan.create(BOOK_ID, USER_ID);

        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(loanManagement.findActiveByUserId(USER_ID)).thenReturn(Collections.emptyList());
        when(loanManagement.save(any(Loan.class))).thenReturn(savedLoan);
        when(bookCatalog.save(any(Book.class))).thenReturn(book);

        Loan result = loanService.borrowBook(new BorrowCommand(BOOK_ID, USER_ID));

        assertNotNull(result);
        assertFalse(book.isAvailable());
        verify(bookCatalog).save(book);
        verify(loanManagement).save(any(Loan.class));
    }

    @Test
    void borrowBook_bookNotFound_throwsException() {
        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> loanService.borrowBook(new BorrowCommand(BOOK_ID, USER_ID)));
    }

    @Test
    void borrowBook_bookNotAvailable_throwsException() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", false);
        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class,
                () -> loanService.borrowBook(new BorrowCommand(BOOK_ID, USER_ID)));
    }

    @Test
    void borrowBook_userNotFound_throwsException() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", true);
        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> loanService.borrowBook(new BorrowCommand(BOOK_ID, USER_ID)));
    }

    @Test
    void borrowBook_userHasThreeLoans_throwsException() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", true);
        User user = new User(USER_ID, "Alice", "alice@example.com");
        List<Loan> threeLoans = List.of(
                Loan.create(new BookId(10L), USER_ID),
                Loan.create(new BookId(11L), USER_ID),
                Loan.create(new BookId(12L), USER_ID)
        );

        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(loanManagement.findActiveByUserId(USER_ID)).thenReturn(threeLoans);

        assertThrows(BorrowLimitExceededException.class,
                () -> loanService.borrowBook(new BorrowCommand(BOOK_ID, USER_ID)));
    }

    @Test
    void borrowBook_userHasOverdueLoans_throwsException() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", true);
        User user = new User(USER_ID, "Alice", "alice@example.com");
        Loan overdueLoan = Loan.reconstitute(new LoanId(99L), new BookId(10L), USER_ID,
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(16), false);

        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(loanManagement.findActiveByUserId(USER_ID)).thenReturn(List.of(overdueLoan));

        assertThrows(BorrowLimitExceededException.class,
                () -> loanService.borrowBook(new BorrowCommand(BOOK_ID, USER_ID)));
    }

    // --- returnBook Tests ---

    @Test
    void returnBook_happyPath_marksReturnedAndRestoresAvailability() {
        Loan loan = Loan.reconstitute(LOAN_ID, BOOK_ID, USER_ID,
                LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), false);
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", false);

        when(loanManagement.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(loanManagement.save(any(Loan.class))).thenReturn(loan);
        when(bookCatalog.save(any(Book.class))).thenReturn(book);

        Loan result = loanService.returnBook(LOAN_ID);

        assertTrue(result.isReturned());
        assertTrue(book.isAvailable());
    }

    @Test
    void returnBook_loanNotFound_throwsException() {
        LoanId unknownId = new LoanId(99L);
        when(loanManagement.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.returnBook(unknownId));
    }

    @Test
    void returnBook_alreadyReturned_throwsException() {
        Loan loan = Loan.reconstitute(LOAN_ID, BOOK_ID, USER_ID,
                LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), true);

        when(loanManagement.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class, () -> loanService.returnBook(LOAN_ID));
    }

    // --- searchBooks Tests ---

    @Test
    void searchBooks_delegatesToBookCatalog() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", true);
        when(bookCatalog.findByTitleContaining("Clean")).thenReturn(List.of(book));

        List<Book> result = loanService.searchBooks("Clean");

        assertEquals(1, result.size());
        assertEquals("Clean Architecture", result.get(0).getTitle());
    }

    // --- findBookDetailById Tests ---

    @Test
    void findBookDetailById_availableBook_returnsDetailWithoutLoanInfo() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", true);
        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));

        BookDetail detail = loanService.findBookDetailById(BOOK_ID);

        assertTrue(detail.available());
        assertNull(detail.borrowedBy());
    }

    @Test
    void findBookDetailById_borrowedBook_returnsDetailWithLoanInfo() {
        Book book = new Book(BOOK_ID, TEST_ISBN, "Clean Architecture", "Robert C. Martin", false);
        Loan loan = Loan.reconstitute(LOAN_ID, BOOK_ID, USER_ID,
                LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), false);
        User user = new User(USER_ID, "Alice", "alice@example.com");

        when(bookCatalog.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(loanManagement.findActiveByBookId(BOOK_ID)).thenReturn(List.of(loan));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        BookDetail detail = loanService.findBookDetailById(BOOK_ID);

        assertFalse(detail.available());
        assertEquals("Alice", detail.borrowedBy());
    }
}
