package com.libraryflow.app.ports.driven.fornotifications;

import java.time.LocalDate;

/**
 * Driven Port für den Versand von Benachrichtigungen.
 *
 * In der hexagonalen Architektur definiert dieser Port die Schnittstelle für Benachrichtigungen.
 * Mehrere Driven Adapter können diesen Port implementieren (z.B. Mail, Push),
 * um verschiedene Benachrichtigungskanäle zu bedienen.
 */
public interface NotificationSender {

    void sendOverdueNotification(String email, String userName, String bookTitle, LocalDate dueDate);
}
