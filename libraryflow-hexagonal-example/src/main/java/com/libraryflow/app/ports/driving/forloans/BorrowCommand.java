package com.libraryflow.app.ports.driving.forloans;

import com.libraryflow.app.model.BookId;
import com.libraryflow.app.model.UserId;

/**
 * Command-Objekt für den Ausleihvorgang.
 * Driving Ports nutzen Command-Objekte, um die Schnittstelle zwischen Adapter und Domäne zu definieren.
 */
public record BorrowCommand(BookId bookId, UserId userId) {
}
