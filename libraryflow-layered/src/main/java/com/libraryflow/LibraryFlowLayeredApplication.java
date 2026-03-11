package com.libraryflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryFlowLayeredApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryFlowLayeredApplication.class, args);
    }
}
