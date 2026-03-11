package com.libraryflow.service;

import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import com.libraryflow.infrastructure.repository.BookRepository;
import com.libraryflow.infrastructure.repository.LoanRepository;
import com.libraryflow.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Schmerzpunkt: @SpringBootTest - kompletter Kontext für einen einfachen Unit Test
// Schmerzpunkt: DREI @MockitoBean - LoanService hat zu viele Abhängigkeiten
@SpringBootTest
class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @MockitoBean
    private LoanRepository loanRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldBorrowAvailableBook() {
        // Aufwändiges Setup: Buch, User und leere Loan-Liste
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);
        book.setAvailable(true);

        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.findByUserIdAndReturnedFalse(1L)).thenReturn(List.of());
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan loan = loanService.borrowBook(1L, 1L);

        assertThat(loan.getBook()).isEqualTo(book);
        assertThat(loan.getUser()).isEqualTo(user);
        assertThat(loan.getBorrowDate()).isEqualTo(LocalDate.now());
        assertThat(loan.getDueDate()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(loan.isReturned()).isFalse();
        verify(bookRepository).save(book);
    }

    @Test
    void shouldRejectBorrowWhenBookNotAvailable() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);
        book.setAvailable(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> loanService.borrowBook(1L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nicht verfügbar");
    }

    @Test
    void shouldRejectBorrowWhenLimitReached() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);
        book.setAvailable(true);

        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        // Aufwändiges Setup: 3 bestehende Ausleihen erstellen
        Loan loan1 = new Loan();
        Loan loan2 = new Loan();
        Loan loan3 = new Loan();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.findByUserIdAndReturnedFalse(1L)).thenReturn(List.of(loan1, loan2, loan3));

        assertThatThrownBy(() -> loanService.borrowBook(1L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("bereits 3 Bücher");
    }

    @Test
    void shouldRejectBorrowWhenUserHasOverdueLoans() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);
        book.setAvailable(true);

        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        Loan overdueLoan = new Loan();
        overdueLoan.setDueDate(LocalDate.now().minusDays(1));
        overdueLoan.setReturned(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.findByUserIdAndReturnedFalse(1L)).thenReturn(List.of(overdueLoan));

        assertThatThrownBy(() -> loanService.borrowBook(1L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("überfällige");
    }

    @Test
    void shouldRejectBorrowWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrowBook(99L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nicht gefunden");
    }

    @Test
    void shouldRejectBorrowWhenUserNotFound() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);
        book.setAvailable(true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrowBook(1L, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nicht gefunden");
    }

    @Test
    void shouldReturnBook() {
        Book book = new Book("978-0-13-468599-1", "Clean Architecture", "Robert C. Martin");
        book.setId(1L);
        book.setAvailable(false);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setReturned(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan returned = loanService.returnBook(1L);

        assertThat(returned.isReturned()).isTrue();
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    void shouldRejectReturnWhenLoanNotFound() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnBook(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nicht gefunden");
    }

    @Test
    void shouldRejectReturnWhenAlreadyReturned() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setReturned(true);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnBook(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("bereits zurückgegeben");
    }

    @Test
    void shouldReturnActiveLoansForUser() {
        Loan loan = new Loan();
        loan.setId(1L);

        when(loanRepository.findByUserIdAndReturnedFalse(1L)).thenReturn(List.of(loan));

        List<Loan> loans = loanService.getActiveLoansForUser(1L);

        assertThat(loans).hasSize(1);
    }
}
