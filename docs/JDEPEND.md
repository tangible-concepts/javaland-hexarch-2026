# Architektur-Metriken mit JDepend

JDepend analysiert die Package-Abhängigkeiten im compilierten Code und liefert objektive Metriken zur Architekturqualität.

---

## Ausführung

```bash
# Alle Module analysieren
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate

# Nur Version 1 (Schichten-Architektur)
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate -pl libraryflow-layered

# Nur Version 2 (Hexagonale Architektur – roter Stand)
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate -pl libraryflow-hexagonal-red

# Nur Version 3 (Hexagonale Architektur – blauer Stand)
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate -pl libraryflow-hexagonal-blue

# Nur Version 4 (Hexagonale Architektur – grüner Stand)
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate -pl libraryflow-hexagonal-green
```

Die Reports liegen danach unter:
- `libraryflow-layered/target/jdepend-report.xml`
- `libraryflow-hexagonal-red/target/jdepend-report.xml`
- `libraryflow-hexagonal-blue/target/jdepend-report.xml`
- `libraryflow-hexagonal-green/target/jdepend-report.xml`

---

## Metriken erklärt

Für jedes Package werden folgende Werte berechnet:

| Metrik | Name | Bedeutung |
|--------|------|-----------|
| **Ca** | Afferent Coupling | Wie viele andere Packages hängen von mir ab? (Eingehende Abhängigkeiten) |
| **Ce** | Efferent Coupling | Von wie vielen Packages hänge ich ab? (Ausgehende Abhängigkeiten) |
| **A** | Abstractness | Anteil abstrakter Klassen/Interfaces (0 = alles konkret, 1 = alles abstrakt) |
| **I** | Instability | Ce / (Ca + Ce). Wie anfällig für Änderungen? (0 = stabil, 1 = instabil) |
| **D** | Distance | Abstand zur "Main Sequence" (ideale Balance aus A und I). Näher an 0 = besser. |

### Faustregeln

- **Hoher Ca** = Viele hängen von mir ab. Änderungen hier haben große Auswirkungen.
- **Hoher Ce** = Ich hänge von vielen ab. Jede Änderung dort kann mich betreffen.
- **D nahe 0** = Gute Balance zwischen Stabilität und Abstraktheit.
- **Zyklen** = Packages, die sich gegenseitig abhängig sind. Sollte es nicht geben.

---

## Ergebnisse: Version 1 (Schichten-Architektur)

```bash
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate -pl libraryflow-layered
```

| Package | Klassen | Ca | Ce | A | I | D |
|---------|---------|----|----|-----|------|------|
| `controller` | 3 | 0 | 11 | 0 | 1.0 | 0 |
| `service` | 3 | 1 | 12 | 0 | 0.92 | 0.08 |
| `model` | 3 | 2 | 3 | 0 | 0.6 | 0.4 |
| `repository` | 3 | 1 | 3 | 1.0 | 0.75 | 0.75 |

### Was fällt auf?

**`controller`-Package: Ce = 11**
Das Controller-Package hat 11 ausgehende Abhängigkeiten. Controller orchestrieren mehrere Services (`BookService` + `LoanService`, `UserService` + `LoanService`), transformieren Daten und berechnen abgeleitete Werte (z.B. `daysRemaining`, `overdueLoans`). Diese Logik müsste bei einer neuen Schnittstelle (z.B. GraphQL) komplett dupliziert werden.

**`service`-Package: Ce = 12**
Das Service-Package hat 12 ausgehende Abhängigkeiten, darunter:
- `com.libraryflow.model` (eigenes Domain-Modell)
- `com.libraryflow.repository` (Datenzugriff)
- `org.springframework.http` (HTTP-Konzepte!)
- `org.springframework.web.server` (Web-Konzepte!)
- `org.springframework.beans.factory.annotation` (@Autowired)
- `org.springframework.stereotype` (@Service)
- `org.springframework.transaction.annotation` (@Transactional)

Die Geschäftslogik ist an HTTP, Web und Spring gekoppelt. Ein Wechsel des Frameworks oder eine neue Schnittstelle (z.B. CLI, Message Queue) erfordert Änderungen am Service.

**`model`-Package: hängt von `jakarta.persistence` ab**
Das Domain-Modell ist an JPA gebunden. Ein Wechsel der Persistenz-Technologie (z.B. MongoDB) erfordert Änderungen am Modell.

**Keine Abstraktheit in `service` und `model` (A = 0)**
Alles sind konkrete Klassen, keine Interfaces. Es gibt keinen Vertrag, gegen den man programmieren könnte.

---

## Ergebnisse: Version 2 (Hexagonale Architektur)

```bash
mvn org.codehaus.mojo:jdepend-maven-plugin:2.0:generate -pl libraryflow-hexagonal-blue
```

| Package | Klassen | Ca | Ce | A | I | D |
|---------|---------|----|----|-----|------|------|
| `domain.model` | 11 | 7 | 7 | 0 | 0.5 | 0.5 |
| `domain.drivingport` | 5 | 3 | 5 | 0.8 | 0.62 | 0.42 |
| `domain.drivenport` | 6 | 3 | 3 | 1.0 | 0.5 | 0.5 |
| `domain.service` | 1 | 1 | 7 | 0 | 0.88 | 0.12 |
| `drivingadapter.rest` | 3 | 0 | 9 | 0 | 1.0 | 0 |
| `drivingadapter.rest.dto` | 5 | 2 | 5 | 0 | 0.71 | 0.29 |
| `drivingadapter.graphql` | 1 | 0 | 9 | 0 | 1.0 | 0 |
| `drivenadapter.persistence` | 10 | 0 | 10 | 0.3 | 1.0 | 0.3 |
| `config` | 1 | 0 | 4 | 0 | 1.0 | 0 |

### Was fällt auf?

**`domain.model`: KEINE Framework-Abhängigkeiten**
Die 7 ausgehenden Abhängigkeiten sind ausschliesslich `java.lang`, `java.time`, `java.util` und eigene Domain-Packages - reines Java, kein Spring, kein JPA, kein Framework. Die 11 Klassen umfassen Rich Domain Model (Book, Loan, User), Domain Records (BookDetail, UserDetail), Value Objects (BookId, UserId, ISBN) und Domain-Exceptions.

**`domain.drivenport`: A = 1.0 (100% abstrakt)**
Nur Interfaces, keine Implementierungen. Die Domain definiert Verträge, die Adapter erfüllen.

**`domain.drivingport`: A = 0.8 (80% abstrakt)**
4 Use-Case-Interfaces + 1 Command-Record. Die Adapter programmieren gegen Interfaces.

**`domain.model`: Ca = 7 (höchste afferente Kopplung)**
7 andere Packages hängen vom Domain-Modell ab. Das ist gewünscht - die Domain ist das Zentrum.

**Adapter: Ca = 0 (niemand hängt von ihnen ab)**
Adapter sind austauschbar. REST kann durch GraphQL ersetzt werden, JPA durch MongoDB - ohne dass sich die Domain ändert.

---

## Direktvergleich

| Aspekt | V1 (Layered) | V2 (Hexagonal) |
|--------|-------------|----------------|
| Framework-Abhängigkeiten in Business-Logik | `service` Ce=12, inkl. Spring HTTP/Web | `domain.service` Ce=7, nur `domain.*` und `java.*` |
| Controller-Kopplung | `controller` Ce=11, orchestriert mehrere Services mit Logik | `drivingadapter.rest` Ce=9, reine Delegation an Use Cases |
| Domain-Modell | hängt von `jakarta.persistence` ab | hängt nur von `java.*` ab |
| Abstraktheit der Schnittstellen | A=0 (keine Interfaces) | `drivingport` A=0.8, `drivenport` A=1.0 |
| Austauschbarkeit der Adapter | nicht möglich (Controller enthält Logik) | Ca=0 (niemand hängt von Adaptern ab) |
| Zyklen | keine | keine |

---

## Für Trainer

### Wann im Workshop einsetzen?

1. **Vormittag (Analyse):** Teilnehmer führen `mvn jdepend:generate -pl libraryflow-layered` aus und analysieren den Report. Fokus auf die Frage: "Von was hängt das service-Package ab?"

2. **Nachmittag (nach Refactoring):** Teilnehmer erzeugen den Report für ihre refaktorierte Version und vergleichen. Fokus: "Was hat sich bei den Abhängigkeiten des Domain-Packages geändert?"

### Diskussionsfragen

- "Warum hat das service-Package Abhängigkeiten zu org.springframework.http?"
- "Was müssten wir ändern, wenn wir statt REST eine CLI-Schnittstelle anbinden wollen?"
- "Was bedeutet es, dass kein anderes Package von den Adaptern abhängt?"
- "Welches Package sollte die höchste afferente Kopplung (Ca) haben und warum?"
