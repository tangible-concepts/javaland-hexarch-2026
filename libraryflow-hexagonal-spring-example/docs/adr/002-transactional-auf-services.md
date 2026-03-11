# ADR-002: Deklaratives Transaktionsmanagement mit @Transactional

## Status

Accepted

## Context

Im bisherigen Modell tragen die Domain Services keine `@Transactional`-Annotation.
Transaktionsgrenzen werden implizit durch Spring Data JPA Repositories gesetzt — jede
einzelne Repository-Methode läuft in einer eigenen Transaktion.

Das ist problematisch für Methoden, die mehrere Schreiboperationen koordinieren:

- `LoanService.borrowBook()`: Liest Buch und User, prüft Verfügbarkeit, aktualisiert Buch-Status,
  erstellt Loan — im Fehlerfall muss alles zurückgerollt werden.
- `LoanService.returnBook()`: Aktualisiert Loan-Status und Buch-Status.

Ohne explizite Transaktion auf Service-Ebene können inkonsistente Zustände entstehen,
wenn eine Operation nach der ersten Schreiboperation fehlschlägt.

## Decision

`@Transactional` wird auf den Methoden `borrowBook()` und `returnBook()` im `LoanService`
verwendet. Diese Methoden koordinieren mehrere Schreiboperationen, die atomar ausgeführt
werden müssen.

Reine Leseoperationen (`searchBooks()`, `findAvailableBooks()`, `findBookDetailById()`)
erhalten kein `@Transactional`, da sie keine Konsistenzanforderungen über mehrere
Repositories hinweg haben.

Andere Services (`CatalogService`, `UserManagementService`, `AdministrationFacade`)
delegieren an einzelne Repository-Aufrufe und benötigen daher kein explizites
Transaktionsmanagement.

## Consequences

**Positiv:**

- Korrekte Transaktionsgrenzen: Mehrere Schreiboperationen in `borrowBook()` und `returnBook()`
  werden atomar ausgeführt.
- Deklarativ und portabel: `@Transactional` ist ein Jakarta-EE-Standard (nicht Spring-spezifisch).
- Inkonsistente Zustände bei Fehlern werden verhindert.

**Negativ:**

- Zusätzliche compile-time-Abhängigkeit auf `org.springframework.transaction.annotation.Transactional`
  im `LoanService`.

**Neutral:**

- Unit-Tests sind nicht betroffen: `@Transactional` hat ohne Spring-Proxy keine Wirkung.
