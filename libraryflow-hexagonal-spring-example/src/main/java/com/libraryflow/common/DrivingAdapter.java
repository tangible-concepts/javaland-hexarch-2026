package com.libraryflow.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet eine Klasse als Driving Adapter in der hexagonalen Architektur.
 *
 * Driving Adapter nehmen externe Anfragen entgegen (z.B. HTTP, CLI) und
 * delegieren an Driving Ports. Erbt {@link org.springframework.stereotype.Component}
 * über {@link HexagonalAdapter}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HexagonalAdapter
public @interface DrivingAdapter {
}
