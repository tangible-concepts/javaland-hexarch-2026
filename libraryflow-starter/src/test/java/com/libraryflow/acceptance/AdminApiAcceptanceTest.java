package com.libraryflow.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminApiAcceptanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllBooks_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/admin/books", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createBook_shouldReturn201() {
        Map<String, Object> request = Map.of(
                "isbn", "978-3-16-148410-0",
                "title", "Test Book",
                "author", "Test Author"
        );
        ResponseEntity<String> response = restTemplate.postForEntity("/api/admin/books", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getAllUsers_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/admin/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createUser_shouldReturn201() {
        Map<String, String> request = Map.of(
                "name", "Test User",
                "email", "test@example.com"
        );
        ResponseEntity<String> response = restTemplate.postForEntity("/api/admin/users", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getUserById_existing_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/admin/users/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserById_notExisting_shouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/admin/users/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
