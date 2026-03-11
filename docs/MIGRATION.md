# Migration Guide: Schichten-Architektur zur Hexagonalen Architektur

Dieser Guide beschreibt Schritt für Schritt, wie das Projekt von der Schichten-Architektur (Version 1) zur hexagonalen Architektur (Version 2) refaktoriert wird.

---

## Meilenstein 1: Domain isolieren

**Ziel:** Die Geschäftslogik aus den Services in Domain-Objekte verschieben.

### Schritt 1.1: Value Objects erstellen

Erstelle typsichere IDs als Records:

```java
// domain/model/BookId.java
public record BookId(Long value) {
    public static BookId of(Long value) {
        return new BookId(value);
    }
}

// domain/model/UserId.java
public record UserId(Long value) { ... }

// domain/model/ISBN.java - mit Validierung!
public record ISBN(String value) {
    public ISBN {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ISBN darf nicht leer sein");
        }
    }
}
```

### Schritt 1.2: Rich Domain Model erstellen

Verschiebe die Geschäftslogik vom Service in die Domain-Objekte. Erstelle neue POJOs (keine JPA-Entities!):

```java
// domain/model/Book.java
public class Book {
    private final BookId id;
    private final ISBN isbn;
    private final String title;
    private final String author;
    private boolean available;

    public void borrowTo(UserId userId) {
        if (!available) {
            throw new BookNotAvailableException(id);
        }
        this.available = false;
    }

    public void returnBook() {
        this.available = true;
    }
}
```

### Schritt 1.3: Domain-Exceptions erstellen

Ersetze `ResponseStatusException` durch domänenspezifische Exceptions:

```java
public class BookNotFoundException extends RuntimeException { ... }
public class BookNotAvailableException extends RuntimeException { ... }
public class BorrowLimitExceededException extends RuntimeException { ... }
```

---

## Meilenstein 2: Ports definieren

**Ziel:** Interfaces definieren, die Ein- und Ausgänge der Domain beschreiben.

### Schritt 2.1: Driving Ports (Eingehende Ports / Use Cases)

```java
// domain/drivingport/BorrowBook.java
public interface BorrowBook {
    Loan borrowBook(BorrowBookCommand command);
}

// domain/drivingport/BorrowBookCommand.java
public record BorrowBookCommand(BookId bookId, UserId userId) {}
```

Weitere Driving Ports: `ReturnBook`, `FindBook`, `ManageUser`

### Schritt 2.2: Driven Ports (Ausgehende Ports)

```java
// domain/drivenport/FindBooks.java
public interface FindBooks {
    Optional<Book> findById(BookId bookId);
    List<Book> findAll();
}

// domain/drivenport/UpdateBook.java
public interface UpdateBook {
    Book update(Book book);
}
```

Weitere Driven Ports: `FindUsers`, `RegisterUser`, `FindLoans`, `RecordLoan`

### Schritt 2.3: Domain-Service implementiert Use Cases

```java
// domain/service/LibraryService.java - Pure Java, kein Spring!
public class LibraryService implements BorrowBook, ReturnBook, FindBook {

    private final FindBooks findBooks;
    private final UpdateBook updateBook;
    // ... weitere Driven Ports

    // Constructor Injection (kein @Autowired!)
    public LibraryService(FindBooks findBooks, UpdateBook updateBook, ...) {
        this.findBooks = findBooks;
        this.updateBook = updateBook;
    }

    @Override
    public Loan borrowBook(BorrowBookCommand command) {
        Book book = findBooks.findById(command.bookId())
            .orElseThrow(() -> new BookNotFoundException(command.bookId()));

        book.borrowTo(command.userId());  // Rich Domain Model!
        updateBook.update(book);

        return recordLoan.record(Loan.create(command.bookId(), command.userId()));
    }
}
```

---

## Meilenstein 3: Adapter erstellen

**Ziel:** Bestehende Controller und Repositories als Adapter umbauen.

### Schritt 3.1: Driven Adapter (Persistence)

Die JPA-Entities bleiben, bekommen aber ein `Entity`-Suffix. Dazu kommt ein Mapper:

```java
// drivenadapter/persistence/BookPersistenceAdapter.java
@Component
public class BookPersistenceAdapter implements FindBooks, UpdateBook {

    private final BookJpaRepository bookJpaRepository;
    private final BookMapper bookMapper;

    @Override
    public Optional<Book> findById(BookId bookId) {
        return bookJpaRepository.findById(bookId.value())
            .map(bookMapper::toDomain);
    }
}
```

### Schritt 3.2: Driving Adapter (REST)

Driving Adapters sprechen nur noch mit Use Cases (nicht mehr mit Services direkt):

```java
// drivingadapter/rest/BookController.java
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
}
```

### Schritt 3.3: Bean Configuration

```java
// config/BeanConfiguration.java
@Configuration
public class BeanConfiguration {

    @Bean
    public LibraryService libraryService(
            FindBooks findBooks,
            UpdateBook updateBook, ...) {
        return new LibraryService(findBooks, updateBook, ...);
    }
}
```

---

## Meilenstein 4: Tests anpassen

### Vorher (Schichten-Architektur)

```java
@SpringBootTest  // Langsam!
class BookServiceTest {
    @MockBean private BookRepository bookRepository;
    @MockBean private LoanRepository loanRepository;
    @MockBean private UserRepository userRepository;
    // Aufwändiges Setup...
}
```

### Nachher (Hexagonale Architektur)

```java
// Plain Java - kein Spring, blitzschnell!
class LibraryServiceTest {
    private FindBooks findBooks = mock(FindBooks.class);
    private RecordLoan recordLoan = mock(RecordLoan.class);
    private LibraryService service = new LibraryService(findBooks, ...);

    @Test
    void shouldBorrowAvailableBook() {
        // Minimales Setup, schnelle Ausführung
    }
}
```

---

## Bonus: GraphQL-Adapter hinzufügen

Demonstriert die Flexibilität: Neue Schnittstelle **ohne Domain-Änderung**!

1. GraphQL-Schema definieren (`resources/graphql/schema.graphqls`)
2. GraphQL-Controller erstellen, der die bestehenden Use Cases nutzt
3. Fertig! Die Domain bleibt unverändert.

---

## Vergleich

| Aspekt | Vorher | Nachher |
|--------|--------|---------|
| Wo lebt die Logik? | Service + Controller | Domain-Objekt |
| Framework-Abhängigkeiten | Überall | Nur in Adaptern |
| `ResponseStatusException` im Service | Ja | Nein (Domain-Exceptions) |
| Controller-Logik | Orchestrierung, Filterung, Berechnung | Reine Delegation an Use Cases |
| Test-Geschwindigkeit | ~3-5 Sekunden | ~50 Millisekunden |
| Test-Setup | Aufwändig (mehrere `@MockBean`) | Minimal (`mock()`) |
| Neue Schnittstelle | Controller-Logik duplizieren | Neuer Adapter (nur Delegation) |
| Abhängigkeitsrichtung | Controller -> Service -> Repository | Adapter -> Domain <- Adapter |
