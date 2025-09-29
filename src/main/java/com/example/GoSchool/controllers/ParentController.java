package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.service.AuthService;
import com.example.GoSchool.service.ParentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    parentDTO.getPassword()
            );

            parentService.createParent(parentDTO, savedUser.getUuid()); // now UUID exists

            return ResponseEntity.ok(Map.of("message", "Parent registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
