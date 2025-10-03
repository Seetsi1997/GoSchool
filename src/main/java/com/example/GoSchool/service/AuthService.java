package com.example.GoSchool.service;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users registerBaseUser(String email, String username, Role role, String rawPassword, String contact) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }

        Users user = new Users();
      //  user.setUuid(uuid);
        user.setEmail(email.toLowerCase());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(capitalizeWords(username));
        user.setPhoneNumber(contact);
        user.setRole(role);
        user.setVerified(false);

        return userRepository.save(user);
    }

    private String capitalizeWords(String input) {
        return Arrays.stream(input.trim().split("\\s+"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
