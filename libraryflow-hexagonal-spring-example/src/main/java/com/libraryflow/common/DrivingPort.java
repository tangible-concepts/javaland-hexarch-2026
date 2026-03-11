package com.libraryflow.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet ein Interface als Driving Port in der hexagonalen Architektur.
 *
 * Driving Ports definieren die fachlichen Anwendungsfälle, die von außen
 * (z.B. REST-API, CLI) aufgerufen werden können.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HexagonalPort
public @interface DrivingPort {
}
