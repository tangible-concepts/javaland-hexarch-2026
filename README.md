# LibraryFlow Workshop

**Vom Schichten-Modell zur hexagonalen Architektur**

Dieses Repository enthält das Beispielprojekt für den ganztägigen Workshop. Es demonstriert den Unterschied zwischen einer klassischen Schichten-Architektur und einer hexagonalen Architektur (Ports & Adapters) anhand einer einfachen Bibliotheksverwaltung.

## Branches

Das Repository ist auf zwei Branches aufgeteilt:

### `main` – Schichten-Architektur & Starter

```
├── libraryflow-starter/             # Startpunkt für Teilnehmer (leeres Skelett)
├── libraryflow-layered/             # Schichten-Architektur (Referenzbeispiel)
└── docs/
    ├── MIGRATION.md                 # Schritt-für-Schritt Refactoring-Guide
    ├── JDEPEND.md                   # Metriken-Auswertung und -Vergleich
    └── TRAINER-NOTES.md             # Hinweise für Trainer
```

### `hexagonal-examples` – Alle Beispiele inkl. hexagonale Varianten

```
├── libraryflow-starter/                    # Startpunkt für Teilnehmer (leeres Skelett)
├── libraryflow-layered/                    # Schichten-Architektur (Referenzbeispiel)
├── libraryflow-hexagonal-bad-example/      # Misslungene hexagonale Überführung
├── libraryflow-hexagonal-example/          # Korrekte hexagonale Architektur
├── libraryflow-hexagonal-spring-example/   # Hexagonal + Spring-Stereotypen
└── docs/
```

## Voraussetzungen

- **Java 17+** (LTS)
- **Maven 3.9+**
- Eine IDE (IntelliJ IDEA empfohlen)

## Schnellstart

```bash
# Repository klonen
git clone <repository-url>
cd hex-arch-javaland

# Projekt bauen und Tests ausführen (main-Branch)
mvn clean install

# Schichten-Architektur starten (Port 8080)
mvn spring-boot:run -pl libraryflow-layered

# Starter starten (Port 8084)
mvn spring-boot:run -pl libraryflow-starter

# Für die hexagonalen Beispiele:
git checkout hexagonal-examples
mvn clean install

# Misslungene hexagonale Überführung starten (Port 8081)
mvn spring-boot:run -pl libraryflow-hexagonal-bad-example

# Korrekte hexagonale Architektur starten (Port 8082)
mvn spring-boot:run -pl libraryflow-hexagonal-example

# Hexagonal + Spring-Stereotypen starten (Port 8083)
mvn spring-boot:run -pl libraryflow-hexagonal-spring-example
```

## Endpunkte

### Alle Versionen (Ports 8080–8084, hexagonale Varianten nur im Branch `hexagonal-examples`)

| Methode | URL | Beschreibung |
|---------|-----|-------------|
| GET | `/api/books/available` | Verfügbare Bücher |
| GET | `/api/books/{id}` | Buch nach ID |
| GET | `/api/books/search?title=` | Bücher suchen |
| POST | `/api/loans/borrow` | Buch ausleihen |
| POST | `/api/loans/{loanId}/return` | Buch zurückgeben |
| GET | `/api/loans/user/{userId}` | Ausleihen eines Nutzers |
| GET | `/api/admin/books` | Alle Bücher (Admin) |
| POST | `/api/admin/books` | Buch anlegen |
| GET | `/api/admin/users` | Alle Nutzer |
| POST | `/api/admin/users` | Nutzer anlegen |
| GET | `/api/admin/users/{id}` | Nutzer nach ID |

### libraryflow-hexagonal-bad-example zusätzlich (Port 8081, Branch `hexagonal-examples`)

| URL | Beschreibung |
|-----|-------------|
| `/graphiql` | GraphQL IDE |
| `/graphql` | GraphQL Endpoint |

### H2 Console

Alle Versionen: `/h2-console`

| Modul | JDBC URL |
|-------|----------|
| libraryflow-layered | `jdbc:h2:mem:libraryflow` |
| libraryflow-hexagonal-bad-example | `jdbc:h2:mem:libraryflow-red` |
| libraryflow-hexagonal-example | `jdbc:h2:mem:libraryflow-blue` |
| libraryflow-hexagonal-spring-example | `jdbc:h2:mem:libraryflow-green` |
| libraryflow-starter | `jdbc:h2:mem:libraryflow-starter` |

User: `sa`, kein Passwort.

## Metriken

```bash
# JDepend-Metriken erzeugen (Abhängigkeiten, Kopplung, Zyklen)
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate -pl libraryflow-layered

# Oder für alle Module
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate
```

Die Berichte finden sich unter `target/jdepend-report.xml` im jeweiligen Modul.
Details zur Ausführung und Interpretation: [JDEPEND.md](docs/JDEPEND.md)

## Workshop-Ablauf

1. **Vormittag:** Theorie + Analyse von Version 1
2. **Nachmittag:** Refactoring von Version 1 zur hexagonalen Architektur (siehe [MIGRATION.md](docs/MIGRATION.md))

## Technischer Stack

- Java 17+
- Spring Boot 3.4.x
- Spring Data JPA
- Spring Web (REST)
- Spring GraphQL (nur libraryflow-hexagonal-bad-example)
- H2 In-Memory Database
- JUnit 5 + Mockito + AssertJ
- ArchUnit 1.3.0 (hexagonale Varianten)
