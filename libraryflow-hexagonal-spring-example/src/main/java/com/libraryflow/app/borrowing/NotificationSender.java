package com.libraryflow.app.borrowing;

import com.libraryflow.common.DrivenPort;

import java.time.LocalDate;

/**
 * Driven Port für den Versand von Benachrichtigungen.
 *
 * In der hexagonalen Architektur definiert dieser Port die Schnittstelle für Benachrichtigungen.
 * Mehrere Driven Adapter können diesen Port implementieren (z.B. Mail, Push),
 * um verschiedene Benachrichtigungskanäle zu bedienen.
 *
 * @see <a href="docs/adr/004-custom-stereotype-annotations.md">ADR-004: Custom Stereotype-Annotationen</a>
 */
@DrivenPort
public interface NotificationSender {

    void sendOverdueNotification(String email, String userName, String bookTitle, LocalDate dueDate);
}
