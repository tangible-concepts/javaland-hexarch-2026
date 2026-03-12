package com.libraryflow.domain.drivenport;

import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface FindUsers {

    Optional<User> findById(UserId userId);

    List<User> findAll();
}
