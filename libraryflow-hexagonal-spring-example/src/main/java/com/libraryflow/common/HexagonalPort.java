package com.libraryflow.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-Annotation für Ports in der hexagonalen Architektur.
 *
 * Wird nicht direkt verwendet, sondern über {@link DrivingPort} und {@link DrivenPort}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HexagonalPort {
}
