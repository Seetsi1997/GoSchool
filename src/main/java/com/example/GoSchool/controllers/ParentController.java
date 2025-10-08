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
    // Get current parent profile
    @GetMapping("/profile")
    public ResponseEntity<ParentDTO> getCurrentParentProfile(Authentication authentication) {
        String email = authentication.getName();

        // Fetch parent entity by email
        Parent parent = parentService.getParentByEmail(email);

        // Convert to DTO
        ParentDTO parentDTO = parentService.getParentToDTO(parent);

        return ResponseEntity.ok(parentDTO);
    }

    // Update current parent profile
    @PutMapping("/profile")
    public ResponseEntity<ParentDTO> updateCurrentParentProfile(
            Authentication authentication,
            @RequestBody ParentDTO updatedParentDTO) {

        String email = authentication.getName();

        // Fetch parent entity
        Parent existingParent = parentService.getParentByEmail(email);

        // Update parent with DTO
        Parent updatedParent = parentService.updateParent(existingParent.getParentUUID(), updatedParentDTO);

        // Convert updated entity to DTO to return
        ParentDTO updatedParentDTOResponse = parentService.getParentToDTO(updatedParent);

        return ResponseEntity.ok(updatedParentDTOResponse);
    }


}
