package com.libraryflow.app.borrowing;

import com.libraryflow.app.shared.*;
import com.libraryflow.app.shared.BookId;
import com.libraryflow.app.shared.LoanId;
import com.libraryflow.app.shared.UserId;
import com.libraryflow.common.DrivenPort;

import java.util.List;
import java.util.Optional;

/**
 * Driven Port für die Verwaltung von Ausleihen.
 *
 * In der hexagonalen Architektur definiert dieser Port die benötigte Persistenz-Schnittstelle
 * für Ausleihen, implementiert durch einen Driven Adapter.
 *
 * @see <a href="docs/adr/004-custom-stereotype-annotations.md">ADR-004: Custom Stereotype-Annotationen</a>
 */
@DrivenPort
public interface LoanManagement {

    Optional<Loan> findById(LoanId id);

    List<Loan> findActiveByUserId(UserId userId);

    List<Loan> findActiveByBookId(BookId bookId);

    List<Loan> findOverdue();

    Loan save(Loan loan);
}
