package com.libraryflow.domain.drivingport;

import com.libraryflow.domain.model.Loan;

public interface ReturnBook {

    Loan returnBook(Long loanId);
}
