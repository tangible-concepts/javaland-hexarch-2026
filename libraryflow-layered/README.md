# LibraryFlow - Schichten-Architektur (Version 1)

Dies ist die **Starter-Version** für den Workshop. Sie zeigt eine typische Schichten-Architektur, wie sie in vielen Projekten vorkommt.

## Starten

```bash
mvn spring-boot:run
# Läuft auf http://localhost:8080
```

## Package-Struktur

```
com.libraryflow/
├── controller/       # REST-Endpunkte
├── service/          # Geschäftslogik
├── repository/       # Datenzugriff (Spring Data JPA)
└── model/            # JPA-Entities
```

## Bekannte Probleme (bewusst eingebaut!)

### 1. Anämisches Domain-Modell
Die Entities (`Book`, `User`, `Loan`) enthalten nur Getter/Setter. Sämtliche Geschäftslogik liegt im Service.

### 2. HTTP-Konzepte im Service
Die Services werfen `ResponseStatusException` - ein HTTP-Konzept hat nichts in der Geschäftslogik zu suchen.

### 3. Field Injection mit @Autowired
Statt Constructor Injection wird überall `@Autowired` auf Feldern verwendet. Das erschwert das Testen.

### 4. Service kennt alle Repositories
Der `LoanService` hat Abhängigkeiten zu `BookRepository`, `UserRepository` und `LoanRepository`. Bei Änderungen an einer Tabelle muss potenziell der gesamte Service angepasst werden.

### 5. Controller orchestriert mehrere Services
`BookController` hängt von `BookService` UND `LoanService` ab, `UserController` von `UserService` UND `LoanService`. Die Controller enthalten Logik: Daten anreichern (`borrowedBy`, `daysRemaining`), filtern (`author`, `available`), berechnen (`overdueLoans`, `remainingBorrowSlots`). Bei einer neuen Schnittstelle (z.B. GraphQL) muss diese Logik dupliziert werden.

### 6. Logik im Controller statt im Service
`BookController.searchBooks()` filtert nach Author und Verfügbarkeit im Controller statt im Repository/Service. `UserController.getUserById()` berechnet Ausleih-Statistiken. Diese Logik gehört eigentlich in die Fachschicht.

### 7. Schwer testbar
- Tests benötigen `@SpringBootTest` (langsam)
- Viele `@MockBean`-Deklarationen nötig
- Controller-Tests brauchen Mocks für mehrere Services
- Aufwändiges Test-Setup mit JPA-Entities

## Aufgabe

Refaktoriere dieses Projekt zur hexagonalen Architektur! Folge dazu dem [MIGRATION.md](../docs/MIGRATION.md).
