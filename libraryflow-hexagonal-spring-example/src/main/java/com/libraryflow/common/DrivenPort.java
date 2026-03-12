package com.libraryflow.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet ein Interface als Driven Port in der hexagonalen Architektur.
 *
 * Driven Ports definieren die Schnittstellen, die der Domänenkern von der
 * Infrastruktur benötigt (z.B. Persistenz, Benachrichtigungen).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HexagonalPort
public @interface DrivenPort {
}
