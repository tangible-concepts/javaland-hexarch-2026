package com.libraryflow.domain.drivingport;

import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.UserId;

public record BorrowBookCommand(BookId bookId, UserId userId) {
}
