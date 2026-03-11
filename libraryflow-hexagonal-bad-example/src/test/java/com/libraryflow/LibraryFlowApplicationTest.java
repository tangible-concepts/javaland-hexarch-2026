package com.libraryflow;

import com.libraryflow.domain.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LibraryFlowApplicationTest {

    @Autowired
    private LibraryService libraryService;

    @Test
    void contextLoads() {
        assertThat(libraryService).isNotNull();
    }
}
