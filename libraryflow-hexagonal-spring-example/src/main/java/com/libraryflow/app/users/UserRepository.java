package com.libraryflow.app.users;

import com.libraryflow.app.shared.UserId;
import com.libraryflow.common.DrivenPort;

import java.util.List;
import java.util.Optional;

/**
 * Driven Port für die Benutzerverwaltung.
 *
 * In der hexagonalen Architektur definiert dieser Port die benötigte Persistenz-Schnittstelle
 * für Benutzer. Die Implementierung erfolgt durch den UserAdapter (Driven Adapter).
 *
 * @see <a href="docs/adr/004-custom-stereotype-annotations.md">ADR-004: Custom Stereotype-Annotationen</a>
 */
@DrivenPort
public interface UserRepository {

    Optional<User> findById(UserId id);

    List<User> findAll();

    User save(User user);
}
