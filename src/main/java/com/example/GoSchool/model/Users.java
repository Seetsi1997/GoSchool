package com.example.GoSchool.model;

import com.example.GoSchool.constant.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "user_email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "username",  nullable = false)
    private String firstName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
  /*  @Column(name = "bio")
    private String bio;*/
    @Column(name = "profile")
    private String profileImageUrl;

    @Column(name = "reset_token")
    private String resetToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "verified")
    private Boolean verified = false;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
    @JsonIgnore
    private Parent parent;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
    @JsonIgnore
    private Driver driver;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users user = (Users) o;
        return Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + uuid +
                ", email='" + email + " " +
                ", name='" + firstName + " " +
                ", verified=" + verified +
                ", role=" + role +
                '}';
    }

}
