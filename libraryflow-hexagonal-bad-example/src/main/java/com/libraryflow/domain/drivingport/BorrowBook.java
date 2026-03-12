package com.libraryflow.domain.drivingport;

import com.libraryflow.domain.model.Loan;

public interface BorrowBook {

    Loan borrowBook(BorrowBookCommand command);
}
