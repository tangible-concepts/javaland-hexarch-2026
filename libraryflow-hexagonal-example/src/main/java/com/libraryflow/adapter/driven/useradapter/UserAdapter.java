package com.libraryflow.adapter.driven.useradapter;

import com.libraryflow.app.model.User;
import com.libraryflow.app.model.UserId;
import com.libraryflow.app.ports.driven.forusermanagement.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Driven Adapter für die Benutzer-Persistenz.
 *
 * Implementiert den Driven Port {@link UserRepository} und übersetzt zwischen
 * dem Domänenmodell und der JPA-Infrastruktur.
 */
@Component
public class UserAdapter implements UserRepository {

    private final UserJpaRepository repository;

    public UserAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return repository.findById(id.value()).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(UserMapper::toDomain).toList();
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = repository.save(entity);
        return UserMapper.toDomain(saved);
    }
}
