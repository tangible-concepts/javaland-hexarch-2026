package com.libraryflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Startpunkt der hexagonalen LibraryFlow-Anwendung (Blue).
 *
 * Die Anwendung folgt dem Ports-and-Adapters-Pattern (Hexagonale Architektur):
 * - Driving Ports definieren die fachlichen Anwendungsfälle (ForLoans, ForAdministration)
 * - Driven Ports abstrahieren die Infrastruktur (ForCatalogManagement, ForUserManagement, ForNotifications)
 * - Adapter verbinden die Ports mit der Außenwelt (REST-API, JPA, Mail/Push)
 */
@SpringBootApplication
@EnableScheduling
public class LibraryFlowBlueApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryFlowBlueApplication.class, args);
    }
}
