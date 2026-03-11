package com.libraryflow.service;

import com.libraryflow.model.User;
import com.libraryflow.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Schmerzpunkt: Auch hier @SpringBootTest - ganzer Spring-Kontext nur um UserService zu testen
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void shouldReturnAllUsers() {
        User user1 = new User("Alice Schmidt", "alice@example.com");
        user1.setId(1L);
        User user2 = new User("Bob Müller", "bob@example.com");
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("Alice Schmidt");
    }

    @Test
    void shouldFindUserById() {
        User user = new User("Alice Schmidt", "alice@example.com");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result.getName()).isEqualTo("Alice Schmidt");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nicht gefunden");
    }

    @Test
    void shouldCreateUser() {
        User user = new User("Alice Schmidt", "alice@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertThat(result.getName()).isEqualTo("Alice Schmidt");
    }

    @Test
    void shouldRejectUserWithoutName() {
        User user = new User();
        user.setEmail("alice@example.com");

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Name");
    }

    @Test
    void shouldRejectUserWithoutEmail() {
        User user = new User();
        user.setName("Alice Schmidt");

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("E-Mail");
    }
}
