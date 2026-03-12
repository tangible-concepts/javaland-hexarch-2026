package com.libraryflow.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet eine Klasse als Driven Adapter in der hexagonalen Architektur.
 *
 * Driven Adapter implementieren Driven Ports und stellen die Verbindung zur
 * Infrastruktur her (z.B. Datenbank, externe Services). Erbt
 * {@link org.springframework.stereotype.Component} über {@link HexagonalAdapter}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HexagonalAdapter
public @interface DrivenAdapter {
}
