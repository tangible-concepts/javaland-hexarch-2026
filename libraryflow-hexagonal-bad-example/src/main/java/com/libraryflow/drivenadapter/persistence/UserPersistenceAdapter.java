package com.libraryflow.drivenadapter.persistence;

import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivenport.FindUsers;
import com.libraryflow.domain.drivenport.RegisterUser;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserPersistenceAdapter implements FindUsers, RegisterUser {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userJpaRepository.findById(userId.value())
                .map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public User register(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return toDomain(saved);
    }

    private User toDomain(UserEntity entity) {
        return new User(
                UserId.of(entity.getId()),
                entity.getName(),
                entity.getEmail()
        );
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId() != null ? user.getId().value() : null,
                user.getName(),
                user.getEmail()
        );
    }
}
