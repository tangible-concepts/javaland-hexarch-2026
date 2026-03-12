package com.libraryflow.app.administration;

import com.libraryflow.app.catalog.Book;
import com.libraryflow.app.catalog.ISBN;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.User;
import com.libraryflow.common.DrivingPort;

import java.util.List;

/**
 * Driving Port für Administrationsfunktionen (Katalog- und Benutzerverwaltung).
 *
 * In der hexagonalen Architektur definiert dieser Driving Port die Anwendungsfälle
 * für administrative Aufgaben. Die Implementierung erfolgt über eine Facade,
 * die an spezialisierte Domain-Services delegiert.
 *
 * @see <a href="docs/adr/004-custom-stereotype-annotations.md">ADR-004: Custom Stereotype-Annotationen</a>
 */
@DrivingPort
public interface ForAdministration {

    Book createBook(ISBN isbn, String title, String author);

    List<Book> findAllBooks();

    User createUser(String name, String email);

    List<User> findAllUsers();

    UserDetail findUserDetailById(UserId userId);
}
