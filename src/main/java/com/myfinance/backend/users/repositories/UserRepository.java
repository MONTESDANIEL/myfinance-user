package com.myfinance.backend.users.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.myfinance.backend.users.entities.user.AppUser;

public interface UserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
}