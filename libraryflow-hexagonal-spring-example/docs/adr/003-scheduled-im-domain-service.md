# ADR-003: @Scheduled direkt im DunningService

## Status

Accepted

## Context

Der `DunningService` prüft täglich überfällige Ausleihen und benachrichtigt Benutzer.
Die zeitgesteuerte Ausführung wird über `@Scheduled(cron = "0 0 0 * * *")` konfiguriert.

In einer strikt hexagonalen Architektur würde `@Scheduled` nicht im Domain Service stehen.
Stattdessen gäbe es einen separaten Driving Adapter — z.B. einen `ScheduledDunningAdapter` —
der den Domain Service aufruft. Dieses Muster würde:

- Eine zusätzliche Adapter-Klasse erfordern, die nur `dunningService.checkOverdueLoans()` aufruft.
- Einen zusätzlichen Driving Port (Interface) erfordern, obwohl es nur eine einzige Methode gibt.
- Die Indirektion erhöhen, ohne funktionalen Mehrwert zu bieten.

Der `DunningService` verwendet bereits `@Scheduled` seit der Einführung des Mahnwesens.
Diese Entscheidung wurde als "pragmatische Ausnahme" dokumentiert und hat sich in der
Praxis bewährt.

## Decision

`@Scheduled` bleibt direkt auf der Methode `DunningService.checkOverdueLoans()`.
Es wird kein separater Scheduler-Adapter eingeführt.

### Begründung

1. **Unverhältnismäßiger Aufwand**: Ein separater Adapter würde eine Klasse und ein Interface
   erzeugen, die ausschließlich eine parameterlose Methode delegieren. Der Overhead übersteigt
   den architektonischen Nutzen.

2. **Keine Austauschbarkeit nötig**: Der Scheduling-Mechanismus (Cron) ist eine stabile
   Infrastrukturentscheidung. Ein Wechsel von Spring Scheduling zu z.B. Quartz würde auch
   mit einem Adapter eine Anpassung erfordern.

3. **Konsistenz mit ADR-001**: Da Domain Services nun ohnehin Spring-Annotationen tragen
   (`@Service`), ist `@Scheduled` keine zusätzliche Kopplung mehr — die Abhängigkeit auf
   das Spring-Framework besteht bereits.

4. **Testbarkeit nicht beeinträchtigt**: `@Scheduled` hat in Unit-Tests keine Wirkung.
   Die Methode `checkOverdueLoans()` kann direkt aufgerufen und getestet werden.

## Consequences

**Positiv:**

- Kein zusätzlicher Adapter oder Port nötig — weniger Boilerplate.
- Die Scheduling-Konfiguration ist direkt an der fachlichen Methode sichtbar.
- Konsistent mit der Entscheidung aus ADR-001, Spring-Annotationen im `app`-Paket zuzulassen.

**Negativ:**

- Der Domain Service hat eine direkte Abhängigkeit auf Spring Scheduling.
- Bei einem hypothetischen Framework-Wechsel muss der Scheduling-Mechanismus im Service
  angepasst werden (statt nur im Adapter).

**Neutral:**

- Keine Änderung am bestehenden Code nötig — `@Scheduled` war bereits vorhanden.
