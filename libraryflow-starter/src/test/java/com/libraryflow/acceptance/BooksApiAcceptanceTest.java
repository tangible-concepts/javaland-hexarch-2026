package com.libraryflow.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksApiAcceptanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAvailableBooks_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/books/available", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getBookById_existing_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/books/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getBookById_notExisting_shouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/books/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void searchBooksByTitle_shouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/books/search?title=Clean", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
