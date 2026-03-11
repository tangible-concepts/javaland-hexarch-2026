package com.libraryflow.controller;

import com.libraryflow.model.Book;
import com.libraryflow.model.Loan;
import com.libraryflow.service.BookService;
import com.libraryflow.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDetailResponse getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);

        BookDetailResponse response = new BookDetailResponse();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setAvailable(book.isAvailable());

        if (!book.isAvailable()) {
            List<Loan> activeLoans = loanService.getActiveLoansForBook(book.getId());
            if (!activeLoans.isEmpty()) {
                Loan activeLoan = activeLoans.get(0);
                response.setBorrowedBy(activeLoan.getUser().getName());
                response.setDueDate(activeLoan.getDueDate());
                response.setDaysRemaining(ChronoUnit.DAYS.between(LocalDate.now(), activeLoan.getDueDate()));
            }
        }

        return response;
    }

    @GetMapping("/available")
    public List<Book> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(
            @RequestParam String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean available) {

        List<Book> results = bookService.searchBooks(title);

        if (author != null && !author.isBlank()) {
            results = results.stream()
                    .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (available != null) {
            results = results.stream()
                    .filter(book -> book.isAvailable() == available)
                    .collect(Collectors.toList());
        }

        return results;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }
}
