package com.libraryflow.domain.drivenport;

import com.libraryflow.domain.model.Loan;

public interface RecordLoan {

    Loan record(Loan loan);
}
