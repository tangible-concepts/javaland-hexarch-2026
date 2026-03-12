package com.libraryflow.adapter.driving.administrationapi;

/**
 * DTO für die Benutzeranlage über die Admin-REST-API.
 */
public record UserRequest(String name, String email) {
}
