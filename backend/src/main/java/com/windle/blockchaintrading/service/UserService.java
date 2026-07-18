package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.entity.User;

import java.util.List;


public interface UserService {
    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
    void registerUser(String username, String email, String password, String fullName);
    List<User> getAllUsers();
    User getUserById(Long id);
    void updateUser(Long id, String username, String email, String fullName);
    void deleteUser(Long id);

}
