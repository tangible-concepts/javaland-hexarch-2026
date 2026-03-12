package com.libraryflow.app.catalog;

import com.libraryflow.app.shared.BookId;

import java.time.LocalDate;

/**
 * Read Model für Buchdetails inklusive Ausleih-Informationen.
 * Im hexagonalen Modell wird dieses Read Model von Driving Ports zurückgegeben,
 * um Adapter von der internen Domänenstruktur zu entkoppeln.
 */
public record BookDetail(
        BookId id,
        ISBN isbn,
        String title,
        String author,
        boolean available,
        String borrowedBy,
        LocalDate dueDate,
        long daysRemaining
) {
}
