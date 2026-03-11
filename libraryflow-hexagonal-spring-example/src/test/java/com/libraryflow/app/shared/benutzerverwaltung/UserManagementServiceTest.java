package com.libraryflow.app.shared.benutzerverwaltung;

import com.libraryflow.app.shared.*;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserManagementService service;

    @BeforeEach
    void setUp() {
        service = new UserManagementService(userRepository);
    }

    @Test
    void createUser_happyPath() {
        User user = new User(new UserId(1L), "Alice", "alice@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = service.createUser("Alice", "alice@example.com");

        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void createUser_emptyName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.createUser("", "alice@example.com"));
    }

    @Test
    void createUser_emptyEmail_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.createUser("Alice", ""));
    }

    @Test
    void findAllUsers_returnsList() {
        User user = new User(new UserId(1L), "Alice", "alice@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = service.findAllUsers();
        assertEquals(1, result.size());
    }
}
