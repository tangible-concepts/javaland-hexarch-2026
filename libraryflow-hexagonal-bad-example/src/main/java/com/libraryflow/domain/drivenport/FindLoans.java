package com.libraryflow.domain.drivenport;

import com.libraryflow.domain.model.BookId;
import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface FindLoans {

    Optional<Loan> findById(Long loanId);

    List<Loan> findActiveByUserId(UserId userId);

    List<Loan> findActiveByBookId(BookId bookId);
}
