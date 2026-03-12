package com.libraryflow.adapter.driven.useradapter;

import com.libraryflow.app.shared.UserId;
import com.libraryflow.app.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserAdapter.class)
class UserAdapterTest {

    @Autowired
    private UserAdapter userAdapter;

    @Test
    void save_andFindById_roundTrip() {
        User user = new User(null, "Alice", "alice@example.com");
        User saved = userAdapter.save(user);

        assertNotNull(saved.getId());

        Optional<User> found = userAdapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
    }

    @Test
    void findAll_returnsAllUsers() {
        userAdapter.save(new User(null, "Alice", "alice@example.com"));
        userAdapter.save(new User(null, "Bob", "bob@example.com"));

        List<User> users = userAdapter.findAll();
        assertTrue(users.size() >= 2);
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<User> found = userAdapter.findById(new UserId(999L));
        assertTrue(found.isEmpty());
    }
}
