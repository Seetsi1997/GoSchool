package com.example.GoSchool.controllers;

import com.example.GoSchool.dtos.*;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.UserRepository;
import com.example.GoSchool.service.EmailService;
import com.example.GoSchool.service.TokenBlacklistService;
import com.example.GoSchool.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/api/users")
public  class UserController {

    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;
    private  final EmailService emailService;
  //  private  final VerificationRepository verificationRepository;
    //private final UserProfileService  userProfileService;

    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/gif");

    @Autowired
    public  UserController(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           //EmailService emailService,
                           TokenBlacklistService blacklistService,
                           EmailService email
                           //VerificationRepository verificationRepository,
                           /*UserProfileService  userProfileService*/) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.blacklistService = blacklistService;
        this.emailService = email;

    }

    public static String capitalizeWords(String input) {
        if (input == null || input.isBlank()) {
            return input; // return as is if null or empty
        }

        return Arrays.stream(input.trim().split("\\s+"))
                .filter(word -> !word.isEmpty()) // ignore extra spaces
                .map(word -> Character.toTitleCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO request) {
        // Validate required fields
        if (request.getRole() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Role must not be empty"));
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must not be empty"));
        }

        /*if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }*/

        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username must not be empty"));
        }

        // Map DTO to Entity
        Users user = new Users();
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(capitalizeWords(request.getUsername()));
        user.setRole(request.getRole());
//        user.setVerified(false);
//        user.setVerificationToken(UUID.randomUUID().toString());

        Users savedUser = userRepository.save(user);

//        String encodedToken = URLEncoder.encode(savedUser.getVerificationToken(), StandardCharsets.UTF_8);
        // String link = "http://10.100.3.53:5050/auth/verify?token=" + encodedToken;
        // emailService.sendVerificationEmail(savedUser.getEmail(), link);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginRequest) {
        String email = loginRequest.getEmail().toLowerCase();
        String rawPassword = loginRequest.getPassword();

        System.out.println("Attempting login for email: " + email);

        try {
            // Check if user exists
            Optional<Users> optionalUser = userRepository.findByEmailIgnoreCase(email);
            if (optionalUser.isEmpty()) {
                System.out.println("User not found with email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            Users user = optionalUser.get();
            System.out.println("User found: " + user.getEmail() + ", stored password: " + user.getPassword());

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, rawPassword)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = jwtUtil.generateToken(authentication.getName(), authentication.getAuthorities());

            System.out.println("Login successful for: " + email);

            return ResponseEntity.ok(new UserLoginResponse(
                    token,
                    user.getRole().name(),
                    user.getPhoneNumber(),
                    user.getFirstName(),
                    user.getUuid()
            ));

        } catch (BadCredentialsException e) {
            System.out.println("Bad credentials for email: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid password"));
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed for email: " + email + " | " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication failed"));
        } catch (Exception e) {
            System.out.println("Unexpected error for email: " + email + " | " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email must not be empty"));
        }

        Optional<Users> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
        if (userOpt.isEmpty()) {
            // For security, you may still return a generic message instead of revealing that the email doesn't exist
            return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link will be sent"));
        }

        Users user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link will be sent"));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        Optional<Users> userOpt = userRepository.findByResetToken(request.getToken());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired reset token"));
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }

        Users user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                Date exp = jwtUtil.extractExpiration(token);
                blacklistService.blacklist(token, exp.getTime());
            } catch (JwtException ex) {
                // If token is invalid or expired, we can still blacklist it
                // by using a default expiration time (e.g., current time + some buffer)
                long defaultExpiration = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 hours
                blacklistService.blacklist(token, defaultExpiration);
            }
        }
        return ResponseEntity.ok().body(Map.of("message", "Logout successful"));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }


}