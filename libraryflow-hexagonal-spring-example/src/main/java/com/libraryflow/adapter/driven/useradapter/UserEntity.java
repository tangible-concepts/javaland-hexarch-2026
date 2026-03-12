package com.libraryflow.adapter.driven.useradapter;

import jakarta.persistence.*;

/**
 * JPA-Entity für Benutzer.
 *
 * Im hexagonalen Modell ist die JPA-Entity Teil des Driven Adapters.
 * Das eigene Schema USERS sorgt für Isolation der Datenhaltung.
 */
@Entity
@Table(name = "APP_USER", schema = "USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    public UserEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
