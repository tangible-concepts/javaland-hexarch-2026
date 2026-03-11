# ADR-001: Spring Stereotype-Annotationen in Domain Services

## Status

Accepted

## Context

Im bisherigen hexagonalen Modell (`libraryflow-hexagonal-blue`) sind alle Domain Services
im `app`-Paket frei von Spring-Annotationen. Die Verdrahtung erfolgt über eine zentrale
`BeanConfiguration`, die jeden Service manuell per `@Bean`-Methode instantiiert.

Das erzeugt messbaren Overhead:

- Eine zusätzliche Konfigurationsklasse (`BeanConfiguration`) mit einer `@Bean`-Methode pro Service.
- Bei jeder neuen Service-Klasse muss die BeanConfiguration manuell erweitert werden.
- Die Konstruktor-Aufrufe in der BeanConfiguration duplizieren die Dependency-Struktur,
  die bereits durch die Konstruktoren der Services definiert ist.

Gleichzeitig bietet die strikte Framework-Freiheit im `app`-Paket in der Praxis keinen
zusätzlichen Nutzen: Die Services verwenden ausschließlich Konstruktor-Injection und können
auch mit `@Service` weiterhin per `new` in Unit-Tests instantiiert werden.

## Decision

Alle Domain Services erhalten die Annotation `@Service` (bzw. `@Service` auf der Facade).
Die zentrale `BeanConfiguration` wird entfernt.

Betroffene Klassen:

- `LoanService`
- `CatalogService`
- `UserManagementService`
- `AdministrationFacade`
- `DunningService`

Die Konstruktor-Injection bleibt erhalten — es wird kein `@Autowired` auf Feldern verwendet.

## Consequences

**Positiv:**

- `BeanConfiguration.java` entfällt vollständig (ca. 55 Zeilen weniger).
- Neue Services werden automatisch durch Component-Scan erkannt — kein manuelles Wiring nötig.
- Die Zugehörigkeit einer Klasse als Spring-Bean ist direkt an der Klasse erkennbar.

**Negativ:**

- Domain Services haben eine compile-time-Abhängigkeit auf `org.springframework.stereotype.Service`.
- Ein hypothetischer Wechsel des DI-Frameworks erfordert das Entfernen dieser Annotation.

**Neutral:**

- Unit-Tests sind nicht betroffen: Services werden weiterhin mit `new Service(mock, mock, ...)` instantiiert.
- Integration-Tests sind nicht betroffen: Component-Scan findet die annotierten Klassen automatisch.
