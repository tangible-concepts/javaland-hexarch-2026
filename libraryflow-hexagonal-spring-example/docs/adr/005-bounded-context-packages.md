# ADR-005: Fachliche Module nach Bounded Contexts

## Status

Accepted

## Context

Nach ADR-004 liegen alle Ports, Model-Klassen und Services flach in `app/`, `app/model/` und
`app/service/`. Die fachliche Zugehörigkeit einer Klasse ist nur über den Klassennamen erkennbar
(z.B. `LoanService` → Ausleihe, `CatalogService` → Katalog). Bei wachsender Codebasis erschwert
diese flache Struktur die Navigation und macht fachliche Abhängigkeiten unsichtbar.

Zusätzlich existieren zyklische Abhängigkeiten zwischen fachlichen Bereichen:

- `User.canBorrow(List<Loan>)` — Benutzerverwaltung hängt von Ausleihe ab
- `Book.borrowTo(UserId)` — Katalog hängt von Benutzerverwaltung ab
- `UserManagementService` nutzt `LoanManagement` nur für `findUserDetailById`

## Decision

### Bounded-Context-Packages

Die Domänenklassen werden in vier Bounded-Context-Packages aufgeteilt:

| Package              | Fachbereich        | Klassen                                                                                |
|----------------------|--------------------|----------------------------------------------------------------------------------------|
| `app.catalog`        | Katalogverwaltung  | Book, ISBN, BookDetail, BookCatalog, CatalogService, BookNotFoundException, BookNotAvailableException |
| `app.useradmin`      | Benutzerverwaltung | User, UserRepository, UserManagementService, UserNotFoundException                     |
| `app.borrowing`      | Ausleihe           | Loan, BorrowCommand, LoanManagement, NotificationSender, ForLoans, LoanService, DunningService, LoanNotFoundException, BorrowLimitExceededException |
| `app.administration` | Administration     | ForAdministration, AdministrationService, UserDetail                                   |

Der `administration`-BC ist ein reiner Konsument (Customer-Supplier-Beziehung): Er orchestriert
über die anderen drei BCs hinweg, kein anderer BC hängt von ihm ab. `UserDetail` liegt hier,
weil es BC-übergreifende Daten aus Benutzerverwaltung und Ausleihe aggregiert.

### Shared Kernel: Entity-IDs

Entity-IDs (`BookId`, `UserId`, `LoanId`) werden in `app.model` als Shared Kernel abgelegt.
Alle BCs dürfen diese importieren. IDs sind leichtgewichtige Value Objects ohne Geschäftslogik —
sie dienen der typsicheren Referenzierung über BC-Grenzen.

### Zyklenauflösung

Vor der Paketierung werden vier Refactorings durchgeführt:

1. **`LoanId`-Record** — Analog zu `BookId`/`UserId` wird `Loan.id` von `Long` auf `LoanId` umgestellt.
2. **`Book.borrowTo(UserId)` → `Book.markAsBorrowed()`** — Der `UserId`-Parameter wird nicht genutzt.
   Entfernung vermeidet catalog→useradmin-Kopplung.
3. **`User.canBorrow(List<Loan>)` → `User.canBorrow(int, boolean)`** — Die Loan-Auswertung
   wandert in `LoanService`. User (useradmin) ist unabhängig von Loan (borrowing).
4. **`findUserDetailById`** wandert von `UserManagementService` in `AdministrationService`
   (`app.administration`), der als BC-übergreifender Orchestrator fungiert und `LoanManagement`
   als Dependency erhält.

### Abhängigkeitsfluss (azyklisch)

```
app.model        → (nichts)                            ← Shared Kernel
catalog          → app.model (BookId)
useradmin        → app.model (UserId)
borrowing        → app.model (BookId, UserId, LoanId)
borrowing        → catalog   (Book, BookCatalog, BookDetail, Exceptions)
borrowing        → useradmin (User, UserRepository, UserNotFoundException)
administration   → catalog + useradmin + borrowing     ← Customer-Supplier
```

## Consequences

**Positiv:**

- Fachliche Struktur im Code sichtbar — Bounded Contexts als Packages.
- Azyklischer Abhängigkeitsfluss zwischen BCs.
- Über BC-Grenzen wird nur über Entity-IDs und Ports kommuniziert.
- Klare Ownership: Jede Klasse gehört genau einem BC.

**Negativ:**

- Einmalige Migration aller Import-Statements (Adapter und Tests).
- `borrowing` hat Abhängigkeiten zu `catalog` und `useradmin` — akzeptabel, da Ausleihe
  den fachlichen Kernprozess abbildet, der Bücher und Benutzer zusammenführt.

**Neutral:**

- Adapter-Packages (`adapter/driven/`, `adapter/driving/`) bleiben unverändert.
- Shared-Stereotype-Annotationen (ADR-004) bleiben in `shared/`.
