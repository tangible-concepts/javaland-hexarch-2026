package com.libraryflow.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-Annotation für Adapter in der hexagonalen Architektur.
 *
 * Trägt {@link Component}, damit abgeleitete Annotationen als Spring-Stereotype funktionieren.
 * Wird nicht direkt verwendet, sondern über {@link DrivingAdapter} und {@link DrivenAdapter}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface HexagonalAdapter {
}
