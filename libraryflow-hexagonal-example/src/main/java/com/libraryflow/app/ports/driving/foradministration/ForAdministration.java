package com.libraryflow.app.ports.driving.foradministration;

import com.libraryflow.app.model.*;

import java.util.List;

/**
 * Driving Port für Administrationsfunktionen (Katalog- und Benutzerverwaltung).
 *
 * In der hexagonalen Architektur definiert dieser Driving Port die Anwendungsfälle
 * für administrative Aufgaben. Die Implementierung erfolgt über eine Facade,
 * die an spezialisierte Domain-Services delegiert.
 */
public interface ForAdministration {

    Book createBook(ISBN isbn, String title, String author);

    List<Book> findAllBooks();

    User createUser(String name, String email);

    List<User> findAllUsers();

    UserDetail findUserDetailById(UserId userId);
}
