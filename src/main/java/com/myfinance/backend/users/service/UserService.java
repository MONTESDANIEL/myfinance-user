package com.myfinance.backend.users.service;

import org.springframework.stereotype.Service;

import com.myfinance.backend.users.entities.User;
import com.myfinance.backend.users.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user){
        userRepository.save(user);
    }
}
