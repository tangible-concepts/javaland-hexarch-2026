package com.libraryflow.app.ports.driven.forusermanagement;

import com.libraryflow.app.model.User;
import com.libraryflow.app.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Driven Port für die Benutzerverwaltung.
 *
 * In der hexagonalen Architektur definiert dieser Port die benötigte Persistenz-Schnittstelle
 * für Benutzer. Die Implementierung erfolgt durch den UserAdapter (Driven Adapter).
 */
public interface UserRepository {

    Optional<User> findById(UserId id);

    List<User> findAll();

    User save(User user);
}
