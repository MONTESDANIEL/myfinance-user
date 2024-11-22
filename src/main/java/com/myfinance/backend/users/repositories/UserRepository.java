package com.myfinance.backend.users.repositories;

import org.springframework.data.repository.CrudRepository;

import com.myfinance.backend.users.entities.User;

public interface UserRepository extends CrudRepository<User, Long>{

}
