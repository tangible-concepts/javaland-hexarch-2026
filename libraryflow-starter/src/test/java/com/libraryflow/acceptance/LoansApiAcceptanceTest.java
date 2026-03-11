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
class LoansApiAcceptanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void borrowBook_available_shouldReturn201() {
        Map<String, Long> request = Map.of("bookId", 1L, "userId", 1L);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/loans/borrow", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void borrowBook_alreadyBorrowed_shouldReturn409() {
        Map<String, Long> request = Map.of("bookId", 4L, "userId", 2L);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/loans/borrow", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void returnBook_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/loans/1/return", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getLoansByUser_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/loans/user/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
