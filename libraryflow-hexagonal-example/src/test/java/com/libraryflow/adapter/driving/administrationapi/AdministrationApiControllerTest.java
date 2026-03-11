package com.libraryflow.adapter.driving.administrationapi;

import com.libraryflow.app.model.*;
import com.libraryflow.app.ports.driving.foradministration.ForAdministration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdministrationApiController.class)
class AdministrationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForAdministration forAdministration;

    @Test
    void createBook_returnsCreated() throws Exception {
        Book book = new Book(new BookId(1L), new ISBN("978-0-13-468599-1"),
                "Clean Architecture", "Robert C. Martin", true);
        when(forAdministration.createBook(any(ISBN.class), anyString(), anyString())).thenReturn(book);

        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"978-0-13-468599-1\",\"title\":\"Clean Architecture\",\"author\":\"Robert C. Martin\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Architecture"));
    }

    @Test
    void createBook_invalidISBN_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"invalid\",\"title\":\"Title\",\"author\":\"Author\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllBooks_returnsList() throws Exception {
        Book book = new Book(new BookId(1L), new ISBN("978-0-13-468599-1"),
                "Clean Architecture", "Robert C. Martin", true);
        when(forAdministration.findAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/admin/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Architecture"));
    }

    @Test
    void createUser_returnsCreated() throws Exception {
        User user = new User(new UserId(1L), "Alice", "alice@example.com");
        when(forAdministration.createUser(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void findAllUsers_returnsList() throws Exception {
        User user = new User(new UserId(1L), "Alice", "alice@example.com");
        when(forAdministration.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void findUserDetailById_returnsDetail() throws Exception {
        UserDetail detail = new UserDetail(new UserId(1L), "Alice", "alice@example.com", 1, 0, 2);
        when(forAdministration.findUserDetailById(any(UserId.class))).thenReturn(detail);

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.activeLoans").value(1))
                .andExpect(jsonPath("$.remainingBorrowSlots").value(2));
    }

    @Test
    void findUserDetailById_notFound_returns404() throws Exception {
        when(forAdministration.findUserDetailById(any(UserId.class)))
                .thenThrow(new UserNotFoundException(new UserId(99L)));

        mockMvc.perform(get("/api/admin/users/99"))
                .andExpect(status().isNotFound());
    }
}
