package com.example.GoSchool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "parents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID parentUUID;

    @Column(name = "parent_first_name", nullable = false)
    private String firstName;

    @Column(name = "parent_surname", nullable = false)
    private String surname;

    @Column(name = "parent_contact", nullable = false)
    private String contact;

    @ManyToOne // because Location is an Entity
    @JoinColumn(name = "location_id", nullable = false)
    private Location parentLocation;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Student> children = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private Users userAccount;

    @Override
    public String toString() {
        return "Parent{" +
                "parentUUID=" + parentUUID +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }

}

