package com.libraryflow.drivenadapter.persistence;

import com.libraryflow.domain.model.Book;
import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.ISBN;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BookPersistenceAdapter.class, BookMapper.class})
@TestPropertySource(properties = "spring.sql.init.mode=never")
class BookPersistenceAdapterTest {

    @Autowired
    private BookPersistenceAdapter bookPersistenceAdapter;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Test
    void shouldSaveAndLoadBook() {
        Book book = new Book(null, ISBN.of("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);

        Book saved = bookPersistenceAdapter.update(book);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldLoadBookById() {
        BookEntity entity = new BookEntity(null, "978-0-13-468599-1", "Clean Architecture", "Robert C. Martin", true);
        BookEntity saved = bookJpaRepository.save(entity);

        Optional<Book> result = bookPersistenceAdapter.findById(BookId.of(saved.getId()));

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldReturnEmptyWhenBookNotFound() {
        Optional<Book> result = bookPersistenceAdapter.findById(BookId.of(999L));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldLoadAvailableBooks() {
        bookJpaRepository.save(new BookEntity(null, "978-0-13-468599-1", "Clean Architecture", "Robert C. Martin", true));
        bookJpaRepository.save(new BookEntity(null, "978-0-13-235088-4", "Clean Code", "Robert C. Martin", false));

        var result = bookPersistenceAdapter.findAvailable();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Architecture");
    }

    @Test
    void shouldLoadAllBooks() {
        bookJpaRepository.save(new BookEntity(null, "978-0-13-468599-1", "Clean Architecture", "Robert C. Martin", true));
        bookJpaRepository.save(new BookEntity(null, "978-0-13-235088-4", "Clean Code", "Robert C. Martin", true));

        var result = bookPersistenceAdapter.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldLoadByTitleContaining() {
        bookJpaRepository.save(new BookEntity(null, "978-0-13-468599-1", "Clean Architecture", "Robert C. Martin", true));
        bookJpaRepository.save(new BookEntity(null, "978-0-13-235088-4", "Clean Code", "Robert C. Martin", true));
        bookJpaRepository.save(new BookEntity(null, "978-0-20-161622-4", "The Pragmatic Programmer", "David Thomas", true));

        var result = bookPersistenceAdapter.findByTitleContaining("Clean");

        assertThat(result).hasSize(2);
    }
}
