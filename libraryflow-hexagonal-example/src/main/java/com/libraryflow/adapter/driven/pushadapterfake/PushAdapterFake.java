package com.libraryflow.adapter.driven.pushadapterfake;

import com.libraryflow.app.ports.driven.fornotifications.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Fake-Implementierung des Driven Ports {@link NotificationSender} für Push-Benachrichtigungen.
 *
 * In der hexagonalen Architektur ist dieser Adapter austauschbar — in einer produktiven Umgebung
 * würde hier ein echter Push-Service (z.B. Firebase) verwendet. Für das Workshop-Beispiel
 * wird der Versand auf der Console simuliert.
 */
@Component
public class PushAdapterFake implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(PushAdapterFake.class);

    @Override
    public void sendOverdueNotification(String email, String userName, String bookTitle, LocalDate dueDate) {
        log.info("[PUSH] Mahnung an {}: Rückgabefrist für '{}' überschritten (fällig am {})",
                userName, bookTitle, dueDate);
    }
}
