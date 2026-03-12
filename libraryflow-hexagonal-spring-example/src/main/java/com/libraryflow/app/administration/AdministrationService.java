package com.libraryflow.app.administration;

import com.libraryflow.app.borrowing.Loan;
import com.libraryflow.app.borrowing.LoanManagement;
import com.libraryflow.app.catalog.*;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade für Administrationsfunktionen.
 *
 * Implementiert den Driving Port {@link ForAdministration} und delegiert an die
 * spezialisierten Domain-Services {@link CatalogService} und {@link UserManagementService}.
 * Enthält BC-übergreifende Logik wie findUserDetailById, die Daten aus
 * Benutzerverwaltung und Ausleihe zusammenführt.
 *
 * @see <a href="docs/adr/001-spring-stereotype-annotations.md">ADR-001: Spring Stereotype-Annotationen</a>
 */
@Service // ADR-001
public class AdministrationService implements ForAdministration {

    private final CatalogService catalogService;
    private final UserManagementService userManagementService;
    private final UserRepository userRepository;
    private final LoanManagement loanManagement;

    public AdministrationService(CatalogService catalogService, UserManagementService userManagementService,
                                 UserRepository userRepository, LoanManagement loanManagement) {
        this.catalogService = catalogService;
        this.userManagementService = userManagementService;
        this.userRepository = userRepository;
        this.loanManagement = loanManagement;
    }

    @Override
    public Book createBook(ISBN isbn, String title, String author) {
        return catalogService.createBook(isbn, title, author);
    }

    @Override
    public List<Book> findAllBooks() {
        return catalogService.findAllBooks();
    }

    @Override
    public User createUser(String name, String email) {
        return userManagementService.createUser(name, email);
    }

    @Override
    public List<User> findAllUsers() {
        return userManagementService.findAllUsers();
    }

    @Override
    public UserDetail findUserDetailById(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Loan> activeLoans = loanManagement.findActiveByUserId(userId);
        long overdueCount = activeLoans.stream().filter(Loan::isOverdue).count();

        return new UserDetail(
                user.getId(),
                user.getName(),
                user.getEmail(),
                activeLoans.size(),
                overdueCount,
                3 - activeLoans.size()
        );
    }
}
