package com.libraryflow.app.borrowing;

import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.UserId;

/**
 * Command-Objekt für den Ausleihvorgang.
 * Driving Ports nutzen Command-Objekte, um die Schnittstelle zwischen Adapter und Domäne zu definieren.
 */
public record BorrowCommand(BookId bookId, UserId userId) {
}
