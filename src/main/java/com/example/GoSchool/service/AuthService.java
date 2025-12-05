package com.example.GoSchool.service;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
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

    public Users registerBaseUser(String email, String firstName, Role role, String rawPassword, String phoneNumber) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be empty");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be empty");
        }
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Users user = new Users();
        user.setEmail(email.toLowerCase());
        user.setFirstName(capitalizeWords(firstName));
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhoneNumber(phoneNumber);

        Role finalRole = (role != null ? role : Role.ADMIN);
        user.setRole(finalRole);

        user.setVerified(finalRole == Role.ADMIN);

        return userRepository.save(user);
    }


    public Users getAdminByEmail(String email) {
        Users user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("This user is not an admin");
        }

        return user;
    }


    private String capitalizeWords(String input) {
        return Arrays.stream(input.trim().split("\\s+"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
