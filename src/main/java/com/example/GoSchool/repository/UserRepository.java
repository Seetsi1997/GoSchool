package com.example.GoSchool.repository;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmailIgnoreCase(String userEmail);
   // Optional<Users> findByVerificationToken(String token);
    Optional<Users> findByResetToken(String resetToken);
    Optional<Users> findByUuid(UUID uuid);
    List<Users> findByRole(Role role);

}
