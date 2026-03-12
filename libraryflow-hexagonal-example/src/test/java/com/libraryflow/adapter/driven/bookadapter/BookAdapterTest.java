package com.libraryflow.adapter.driven.bookadapter;

import com.libraryflow.app.model.Book;
import com.libraryflow.app.model.BookId;
import com.libraryflow.app.model.ISBN;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BookAdapter.class)
class BookAdapterTest {

    @Autowired
    private BookAdapter bookAdapter;

    @Test
    void save_andFindById_roundTrip() {
        Book book = new Book(null, new ISBN("978-0-13-468599-1"), "Clean Architecture", "Robert C. Martin", true);
        Book saved = bookAdapter.save(book);

        assertNotNull(saved.getId());

        Optional<Book> found = bookAdapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Clean Architecture", found.get().getTitle());
    }

    @Test
    void findAvailable_returnsOnlyAvailableBooks() {
        bookAdapter.save(new Book(null, new ISBN("978-0-13-468599-1"), "Available Book", "Author", true));
        bookAdapter.save(new Book(null, new ISBN("978-0-13-235088-4"), "Borrowed Book", "Author", false));

        List<Book> available = bookAdapter.findAvailable();
        assertTrue(available.stream().allMatch(Book::isAvailable));
    }

    @Test
    void findByTitleContaining_findsMatchingBooks() {
        bookAdapter.save(new Book(null, new ISBN("978-0-13-468599-1"), "Clean Architecture", "Author", true));
        bookAdapter.save(new Book(null, new ISBN("978-0-13-235088-4"), "Clean Code", "Author", true));
        bookAdapter.save(new Book(null, new ISBN("978-0-201-63361-0"), "Design Patterns", "Author", true));

        List<Book> result = bookAdapter.findByTitleContaining("Clean");
        assertEquals(2, result.size());
    }

    @Test
    void findAll_returnsAllBooks() {
        bookAdapter.save(new Book(null, new ISBN("978-0-13-468599-1"), "Book 1", "Author", true));
        bookAdapter.save(new Book(null, new ISBN("978-0-13-235088-4"), "Book 2", "Author", true));

        List<Book> all = bookAdapter.findAll();
        assertTrue(all.size() >= 2);
    }
}
