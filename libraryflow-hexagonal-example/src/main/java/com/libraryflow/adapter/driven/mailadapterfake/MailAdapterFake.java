package com.libraryflow.adapter.driven.mailadapterfake;

import com.libraryflow.app.ports.driven.fornotifications.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Fake-Implementierung des Driven Ports {@link NotificationSender} für E-Mail-Benachrichtigungen.
 *
 * In der hexagonalen Architektur ist dieser Adapter austauschbar — in einer produktiven Umgebung
 * würde hier ein echter SMTP-Client verwendet. Für das Workshop-Beispiel wird der Versand
 * auf der Console simuliert.
 */
@Component
public class MailAdapterFake implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(MailAdapterFake.class);

    @Override
    public void sendOverdueNotification(String email, String userName, String bookTitle, LocalDate dueDate) {
        log.info("[MAIL] Mahnung an {}: Hallo {}, die Rückgabefrist für '{}' war am {}. " +
                "Bitte geben Sie das Buch schnellstmöglich zurück.", email, userName, bookTitle, dueDate);
    }
}
