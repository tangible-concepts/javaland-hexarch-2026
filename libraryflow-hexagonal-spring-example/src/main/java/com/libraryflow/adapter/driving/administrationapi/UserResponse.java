package com.libraryflow.adapter.driving.administrationapi;

import com.libraryflow.app.users.User;

/**
 * DTO für die Rückgabe eines Benutzers über die Admin-REST-API.
 */
public record UserResponse(Long id, String name, String email) {

    static UserResponse from(User user) {
        return new UserResponse(user.getId().value(), user.getName(), user.getEmail());
    }
}
