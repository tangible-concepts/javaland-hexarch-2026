# LibraryFlow - Hexagonale Architektur (Roter Stand)

Dies ist die **misslungene** hexagonale Variante. Die Paketstruktur sieht hexagonal aus, aber die Architekturprinzipien werden nicht konsequent eingehalten.

## Starten

```bash
mvn spring-boot:run
# Läuft auf http://localhost:8081
```

## Architektur-Probleme (bewusst eingebaut!)

### 1. Kein echtes Domain-Modell
Die Domain-Klassen (`Book`, `User`, `Loan`) sind zwar im `domain/model`-Package, haben aber keine echte Business-Logik. Das anämische Modell aus Version 1 wurde nur verschoben.

### 2. Domain-Service kennt JPA-Details
Der `LibraryService` arbeitet intern mit Strukturen, die implizit an die Persistence gebunden sind, statt konsequent mit Domain-Objekten zu arbeiten.

### 3. Ports nicht konsequent getrennt
Die Driven Ports sind zu grob geschnitten – sie spiegeln die Repositories wider statt fachliche Operationen zu beschreiben.

### 4. Packages spiegeln keine klare Hexagonale Struktur
`drivingadapter/` und `drivenadapter/` statt `adapter/driving/` und `adapter/driven/` – die Paketkonvention weicht von blue/green ab.

## GraphQL

GraphiQL: http://localhost:8081/graphiql

## Details

Vergleich mit der richtigen Umsetzung: [docs/MIGRATION.md](../docs/MIGRATION.md)
