package com.libraryflow.adapter.driving.loansapi;

/**
 * DTO für die Ausleihanfrage über die REST-API.
 * Adapter-internes Objekt — wird im Controller in ein {@link com.libraryflow.app.ports.driving.forloans.BorrowCommand} übersetzt.
 */
public record BorrowRequest(Long bookId, Long userId) {
}
