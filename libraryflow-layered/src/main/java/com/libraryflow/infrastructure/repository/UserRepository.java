package com.libraryflow.infrastructure.repository;

import com.libraryflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
