package com.myfinance.backend.users.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.myfinance.backend.users.entities.user.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
