# ADR-004: Custom Stereotype-Annotationen und Port-Package-Auflösung

## Status

Accepted

## Context

Die Port-Interfaces liegen in tief verschachtelten Packages (`app.ports.driven.forcatalogmanagement`,
`app.ports.driven.forusermanagement`, etc.). Diese Struktur erzeugt viele fast leere Packages und
erhöht die Navigationstiefe, ohne fachlichen Mehrwert zu bieten. Die Rolle eines Interfaces
(Driving Port, Driven Port) ist nur indirekt über den Package-Pfad erkennbar.

Gleichzeitig fehlt bei den Adaptern eine explizite Kennzeichnung ihrer architektonischen Rolle.
Ob eine `@Component`-Klasse ein Driving Adapter, Driven Adapter oder etwas anderes ist, ergibt
sich nur aus dem Package-Namen und der Konvention.

## Decision

### Custom Stereotype-Annotationen

Es werden sechs Custom-Annotationen im Package `com.libraryflow.common` eingeführt:

| Annotation         | Meta-Annotation      | `@Component` | Verwendung                    |
|--------------------|----------------------|:------------:|-------------------------------|
| `@HexagonalPort`   | —                    | Nein         | Meta-Annotation für Ports     |
| `@HexagonalAdapter`| —                    | Ja           | Meta-Annotation für Adapter   |
| `@DrivingPort`     | `@HexagonalPort`     | Nein         | Driving Port Interfaces       |
| `@DrivenPort`      | `@HexagonalPort`     | Nein         | Driven Port Interfaces        |
| `@DrivingAdapter`  | `@HexagonalAdapter`  | Ja (geerbt)  | REST-Controller, CLI etc.     |
| `@DrivenAdapter`   | `@HexagonalAdapter`  | Ja (geerbt)  | JPA-Adapter, Fake-Adapter etc.|

`@HexagonalAdapter` trägt `@Component`, sodass `@DrivingAdapter` und `@DrivenAdapter` als
Spring-Stereotype funktionieren und das bisherige `@Component` auf den Adaptern ersetzen.

### Port-Package-Auflösung

Die Port-Interfaces wandern aus den verschachtelten Sub-Packages direkt ins `app`-Package:

- `app.ports.driving.forloans.ForLoans` → `app.ForLoans`
- `app.ports.driving.forloans.BorrowCommand` → `app.BorrowCommand`
- `app.ports.driving.foradministration.ForAdministration` → `app.ForAdministration`
- `app.ports.driven.forcatalogmanagement.BookCatalog` → `app.BookCatalog`
- `app.ports.driven.forcatalogmanagement.LoanManagement` → `app.LoanManagement`
- `app.ports.driven.forusermanagement.UserRepository` → `app.UserRepository`
- `app.ports.driven.fornotifications.NotificationSender` → `app.NotificationSender`

Die architektonische Rolle wird durch `@DrivingPort` bzw. `@DrivenPort` auf dem Interface
dokumentiert — nicht mehr durch den Package-Pfad.

### Zyklenfreiheit

- `shared`: Keine Abhängigkeiten (nur Java/Spring-Annotationen)
- `app`: Importiert `shared` (für `@DrivingPort`/`@DrivenPort`)
- `app.model`: Keine Änderung
- `app.service`: Importiert `app` (Ports) und `app.model`
- `adapter`: Importiert `app` (Ports), `app.model`, `shared` (für `@DrivenAdapter`/`@DrivingAdapter`)
- Keine Rückwärts-Abhängigkeit von `app` nach `adapter`

## Consequences

**Positiv:**

- Die architektonische Rolle jeder Klasse ist direkt am Typ sichtbar (`@DrivenAdapter`, `@DrivingPort`).
- Flachere Package-Struktur — weniger Navigation, bessere Übersicht.
- `@DrivenAdapter`/`@DrivingAdapter` ersetzen das generische `@Component` und machen die
  Hexagonal-Architektur im Code explizit.
- Spring Component Scanning funktioniert weiterhin über die geerbte `@Component`-Annotation.

**Negativ:**

- Zusätzliche Annotationen, die gepflegt werden müssen.
- Einmalige Migration aller Import-Statements.

**Neutral:**

- Konsistent mit ADR-001: Spring-Annotationen im Anwendungskern sind bereits akzeptiert.
