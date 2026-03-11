package com.libraryflow.drivenadapter.persistence;

import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserPersistenceAdapter.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class UserPersistenceAdapterTest {

    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void shouldSaveAndLoadUser() {
        User user = new User(null, "Alice", "alice@example.com");

        User saved = userPersistenceAdapter.register(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void shouldLoadUserById() {
        UserEntity entity = new UserEntity(null, "Bob", "bob@example.com");
        UserEntity saved = userJpaRepository.save(entity);

        Optional<User> result = userPersistenceAdapter.findById(UserId.of(saved.getId()));

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Bob");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> result = userPersistenceAdapter.findById(UserId.of(999L));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldLoadAllUsers() {
        userJpaRepository.save(new UserEntity(null, "Alice", "alice@example.com"));
        userJpaRepository.save(new UserEntity(null, "Bob", "bob@example.com"));

        var result = userPersistenceAdapter.findAll();

        assertThat(result).hasSize(2);
    }
}
