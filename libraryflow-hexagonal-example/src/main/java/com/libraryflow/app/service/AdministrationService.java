package com.libraryflow.app.service;

import com.libraryflow.app.model.*;
import com.libraryflow.app.ports.driving.foradministration.ForAdministration;

import java.util.List;

/**
 * Facade für Administrationsfunktionen.
 *
 * Implementiert den Driving Port {@link ForAdministration} und delegiert an die
 * spezialisierten Domain-Services {@link CatalogService} und {@link UserManagementService}.
 * Im hexagonalen Modell bündelt die Facade mehrere fachliche Module hinter einem einzigen Port.
 */
public class AdministrationService implements ForAdministration {

    private final CatalogService catalogService;
    private final UserManagementService userManagementService;

    public AdministrationService(CatalogService catalogService, UserManagementService userManagementService) {
        this.catalogService = catalogService;
        this.userManagementService = userManagementService;
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
        return userManagementService.findUserDetailById(userId);
    }
}
