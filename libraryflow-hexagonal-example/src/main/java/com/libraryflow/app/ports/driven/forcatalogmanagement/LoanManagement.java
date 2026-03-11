package com.libraryflow.app.ports.driven.forcatalogmanagement;

import com.libraryflow.app.model.BookId;
import com.libraryflow.app.model.Loan;
import com.libraryflow.app.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Driven Port für die Verwaltung von Ausleihen.
 *
 * Liegt im Package forcatalogmanagement, da Ausleihen fachlich zum Katalogmanagement gehören.
 * In der hexagonalen Architektur definiert dieser Port die benötigte Persistenz-Schnittstelle
 * für Ausleihen, implementiert durch einen Driven Adapter.
 */
public interface LoanManagement {

    Optional<Loan> findById(Long id);

    List<Loan> findActiveByUserId(UserId userId);

    List<Loan> findActiveByBookId(BookId bookId);

    List<Loan> findOverdue();

    Loan save(Loan loan);
}
