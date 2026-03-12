package com.libraryflow.app.service;

import com.libraryflow.app.model.*;
import com.libraryflow.app.ports.driven.forcatalogmanagement.LoanManagement;
import com.libraryflow.app.ports.driven.forusermanagement.UserRepository;

import java.util.List;

/**
 * Domain Service für die Benutzerverwaltung.
 *
 * Enthält die fachliche Logik für Benutzeranlage und -abfrage.
 * Im hexagonalen Modell ist dieser Service frei von Framework-Annotationen —
 * das Wiring erfolgt über die BeanConfiguration.
 */
public class UserManagementService {

    private final UserRepository userRepository;
    private final LoanManagement loanManagement;

    public UserManagementService(UserRepository userRepository, LoanManagement loanManagement) {
        this.userRepository = userRepository;
        this.loanManagement = loanManagement;
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
