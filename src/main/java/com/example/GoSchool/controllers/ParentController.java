package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.service.AuthService;
import com.example.GoSchool.service.ParentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/api/parents")
public class ParentController {

    private final AuthService authService;
    private final ParentService parentService;
    //private final PasswordEncoder passwordEncoder;

    public ParentController (ParentService parentService, AuthService authService, PasswordEncoder passwordEncoder){
        this.parentService = parentService;
        this.authService = authService;
       // this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerParent(@RequestBody ParentDTO parentDTO) {
        try {
            Users savedUser = authService.registerBaseUser(
                    parentDTO.getEmail(),
                    parentDTO.getFirstName() + " " + parentDTO.getSurname(),
                    Role.PARENT,
                    parentDTO.getPassword(),
                    parentDTO.getContact()
            );

            parentService.createParent(parentDTO, savedUser.getUuid()); // now UUID exists

            return ResponseEntity.ok(Map.of("message", "Parent registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get current logged-in parent (you'll need to implement authentication)
    // Get current parent
    @GetMapping("/me")
    public ResponseEntity<Parent> getCurrentParentProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            Parent parent = parentService.getParentByEmail(email);
            return ResponseEntity.ok(parent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Update current parent
    @PutMapping("/me")
    public ResponseEntity<Parent> updateCurrentParentProfile(
            Authentication authentication,
            @RequestBody Parent updatedParent) {
        try {
            System.out.println("Authentication: " + authentication);
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println("Authorities: " + authentication.getAuthorities());
            System.out.println("Name: " + authentication.getName());

            String email = authentication.getName();
            Parent existingParent = parentService.getParentByEmail(email);


            Parent savedParent = parentService.updateParent(existingParent.getParentUUID(), updatedParent);
            return ResponseEntity.ok(savedParent);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
