package com.libraryflow.drivingadapter.rest;

import com.libraryflow.drivingadapter.rest.dto.BookDTO;
import com.libraryflow.drivingadapter.rest.dto.BookDetailDTO;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.BookNotFoundException;
import com.libraryflow.domain.drivingport.FindBook;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final FindBook findBook;

    public BookController(FindBook findBook) {
        this.findBook = findBook;
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return findBook.findAllBooks().stream()
                .map(BookDTO::from)
                .toList();
    }

    @GetMapping("/{id}")
    public BookDetailDTO getBookById(@PathVariable Long id) {
        return BookDetailDTO.from(findBook.findBookDetailById(BookId.of(id)));
    }

    @GetMapping("/available")
    public List<BookDTO> getAvailableBooks() {
        return findBook.findAvailableBooks().stream()
                .map(BookDTO::from)
                .toList();
    }

    @GetMapping("/search")
    public List<BookDTO> searchBooks(
            @RequestParam String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean available) {
        return findBook.searchBooks(title, author, available).stream()
                .map(BookDTO::from)
                .toList();
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBookNotFound(BookNotFoundException ex) {
        return ex.getMessage();
    }
}
