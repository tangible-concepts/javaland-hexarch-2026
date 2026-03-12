package com.libraryflow.app.administration;

import com.libraryflow.app.shared.UserId;

/**
 * Read Model für Benutzerdetails inklusive Ausleihstatistik.
 * Im hexagonalen Modell wird dieses Read Model von Driving Ports zurückgegeben,
 * um Adapter von der internen Domänenstruktur zu entkoppeln.
 */
public record UserDetail(
        UserId id,
        String name,
        String email,
        int activeLoans,
        long overdueLoans,
        int remainingBorrowSlots
) {
}
