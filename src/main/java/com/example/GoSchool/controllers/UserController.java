package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.*;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.Parent;
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
import org.springframework.security.authentication.AuthenticationManager;import org.springframework.security.authentication.BadCredentialsException;
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

        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username must not be empty"));
        }

        // Map DTO to Entity
        Users user = new Users();
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(capitalizeWords(request.getFirstName()));
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

        try {
            Optional<Users> optionalUser = userRepository.findByEmailIgnoreCase(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Wrong email or password"));
            }

            Users user = optionalUser.get();

            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Wrong password"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, rawPassword)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(authentication.getName(), authentication.getAuthorities());

            UserLoginResponse response = new UserLoginResponse();
            response.setToken(token);
            response.setRole(user.getRole().name());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setFirstName(user.getFirstName());
            response.setEmail(user.getEmail());
            response.setUuid(user.getUuid());

            // PARENT INFO
            if (user.getRole() == Role.PARENT) {
                Parent parent = user.getParent();
                response.setParentUUID(parent.getParentUUID());
                response.setParentFirstName(parent.getFirstName());
            }

            // DRIVER INFO
            if (user.getRole() == Role.DRIVER) {
                Driver driver = user.getDriver();
                if (driver != null) {
                    response.setDriverUUID(driver.getDriverUUID());
                    response.setDriverName(driver.getDriverName());
                   // response.setDriverSurname(driver.getDriverSurname());
                }
            }

            System.out.println("Sending login response: " + response);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Wrong password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
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
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

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

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
                                            HttpServletRequest request) {
        try {
            // Extract token from request
            String token = extractTokenFromRequest(request);
            /*System.out.println("=== CHANGE PASSWORD DEBUG ===");
            System.out.println("Authorization Header: " + request.getHeader("Authorization"));
            System.out.println("Extracted Token: " + (token != null ? "Token exists, length: " + token.length() : "NULL"));

            if (token == null) {
                System.out.println("DEBUG: No token found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No authentication token found"));
            }*/

            // Validate token
            boolean isValidToken = jwtUtil.validateToken(token);
            System.out.println("DEBUG: Token validation result: " + isValidToken);

            if (!isValidToken) {
                System.out.println("DEBUG: Token validation failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Extract user email from token
            String userEmail = jwtUtil.extractUsername(token);
            //System.out.println("DEBUG: Extracted user email: " + userEmail);

            // Validate required fields
            if (changePasswordDTO.getCurrentPassword() == null || changePasswordDTO.getCurrentPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Current password is required"));
            }

            if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password is required"));
            }

            if (changePasswordDTO.getConfirmPassword() == null || changePasswordDTO.getConfirmPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Confirm password is required"));
            }

            // Check if new password and confirm password match
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password and confirm password do not match"));
            }

            // Check if new password is different from current password
            if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getCurrentPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password must be different from current password"));
            }

            // Optional: Add password strength validation
            if (changePasswordDTO.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password must be at least 6 characters long"));
            }

            // Find user
            Optional<Users> userOptional = userRepository.findByEmailIgnoreCase(userEmail);
            if (userOptional.isEmpty()) {
               // System.out.println("DEBUG: User not found for email: " + userEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            Users user = userOptional.get();
            //System.out.println("DEBUG: User found: " + user.getEmail());

            // Verify current password
            boolean passwordMatches = passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword());
            //System.out.println("DEBUG: Current password matches: " + passwordMatches);

            if (!passwordMatches) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Current password is incorrect"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(user);

            System.out.println("DEBUG: Password changed successfully for user: " + userEmail);
            System.out.println("=== END DEBUG ===");

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (Exception e) {
            System.out.println("ERROR in changePassword: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while changing password: " + e.getMessage()));
        }
    }
}