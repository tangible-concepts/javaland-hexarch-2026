package com.libraryflow.adapter.driven.useradapter;

import com.libraryflow.app.model.User;
import com.libraryflow.app.model.UserId;

/**
 * Mapper zwischen Domänenmodell {@link User} und JPA-Entity {@link UserEntity}.
 *
 * Im hexagonalen Modell übersetzt der Mapper an der Grenze zwischen Domäne und Infrastruktur.
 */
class UserMapper {

    static User toDomain(UserEntity entity) {
        return new User(
                new UserId(entity.getId()),
                entity.getName(),
                entity.getEmail()
        );
    }

    static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        if (user.getId() != null) {
            entity.setId(user.getId().value());
        }
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        return entity;
    }
}
