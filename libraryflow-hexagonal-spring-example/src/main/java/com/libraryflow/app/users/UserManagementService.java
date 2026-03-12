package com.libraryflow.app.users;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain Service für die Benutzerverwaltung.
 *
 * Enthält die fachliche Logik für Benutzeranlage und -abfrage.
 *
 * @see <a href="docs/adr/001-spring-stereotype-annotations.md">ADR-001: Spring Stereotype-Annotationen</a>
 */
@Service // ADR-001
public class UserManagementService {

    private final UserRepository userRepository;

    public UserManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name darf nicht leer sein");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-Mail darf nicht leer sein");
        }
        User user = new User(null, name, email);
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
