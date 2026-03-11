package com.libraryflow.adapter.driving.administrationapi;

import com.libraryflow.app.model.*;
import com.libraryflow.app.ports.driving.foradministration.ForAdministration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Driving Adapter für Administrationsfunktionen (Katalog- und Benutzerverwaltung).
 *
 * In der hexagonalen Architektur ist der REST-Controller ein Driving Adapter,
 * der eingehende HTTP-Requests entgegennimmt und an den Driving Port {@link ForAdministration} delegiert.
 */
@RestController
@RequestMapping("/api/admin")
public class AdministrationApiController {

    private final ForAdministration forAdministration;

    public AdministrationApiController(ForAdministration forAdministration) {
        this.forAdministration = forAdministration;
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminBookResponse createBook(@RequestBody BookRequest request) {
        try {
            Book book = forAdministration.createBook(
                    new ISBN(request.isbn()), request.title(), request.author());
            return AdminBookResponse.from(book);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/books")
    public List<AdminBookResponse> findAllBooks() {
        return forAdministration.findAllBooks().stream()
                .map(AdminBookResponse::from).toList();
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UserRequest request) {
        try {
            User user = forAdministration.createUser(request.name(), request.email());
            return UserResponse.from(user);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/users")
    public List<UserResponse> findAllUsers() {
        return forAdministration.findAllUsers().stream()
                .map(UserResponse::from).toList();
    }

    @GetMapping("/users/{id}")
    public UserDetailResponse findUserDetailById(@PathVariable Long id) {
        try {
            UserDetail detail = forAdministration.findUserDetailById(new UserId(id));
            return UserDetailResponse.from(detail);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
