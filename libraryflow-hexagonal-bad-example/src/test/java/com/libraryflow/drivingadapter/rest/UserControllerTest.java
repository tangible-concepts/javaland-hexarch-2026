package com.libraryflow.drivingadapter.rest;

import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserDetail;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.ManageUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManageUser manageUser;

    @Test
    void shouldReturnAllUsers() throws Exception {
        var user = new User(UserId.of(1L), "Alice", "alice@example.com");
        when(manageUser.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void shouldReturnUserDetailWithLoanStats() throws Exception {
        var detail = new UserDetail(UserId.of(1L), "Alice", "alice@example.com", 2, 1, 1);

        when(manageUser.findUserDetailById(UserId.of(1L))).thenReturn(detail);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.activeLoans").value(2))
                .andExpect(jsonPath("$.overdueLoans").value(1))
                .andExpect(jsonPath("$.remainingBorrowSlots").value(1));
    }

    @Test
    void shouldReturnUserDetailWithNoLoans() throws Exception {
        var detail = new UserDetail(UserId.of(1L), "Bob", "bob@example.com", 0, 0, 3);

        when(manageUser.findUserDetailById(UserId.of(1L))).thenReturn(detail);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.activeLoans").value(0))
                .andExpect(jsonPath("$.remainingBorrowSlots").value(3));
    }

    @Test
    void shouldCreateUser() throws Exception {
        var user = new User(UserId.of(1L), "Alice", "alice@example.com");
        when(manageUser.createUser(eq("Alice"), eq("alice@example.com"))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Alice\", \"email\": \"alice@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"));
    }
}
