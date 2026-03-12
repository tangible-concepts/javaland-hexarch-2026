package com.libraryflow.adapter.driving.administrationapi;

/**
 * DTO für die Buchanlage über die Admin-REST-API.
 */
public record BookRequest(String isbn, String title, String author) {
}
