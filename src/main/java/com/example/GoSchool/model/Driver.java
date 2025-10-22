package com.example.GoSchool.model;

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
@Table(name = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID driverUUID;

    @Column(nullable = false)
    private String driverName;

    @Column(nullable = false)
    private String driverSurname;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location driverLocation;

    @Column(nullable = false)
    private int totalNumberOfStudents;

    @Column(nullable = false)
    private String contact;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Student> assignedStudents = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users userAccount;
}


