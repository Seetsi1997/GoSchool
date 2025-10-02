package com.example.GoSchool.model;

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
   // @Column(name = "driver_uuid", updatable = false, nullable = false)
    private UUID driverUUID;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "driver_surname", nullable = false)
    private String driverSurname;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location driverLocation;

    @Column(name = "total_number_of_students", nullable = false)
    private int totalNumberOfStudents;

    @Column(name = "driver_contact", nullable = false)
    private String contact;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> assignedStudents = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users userAccount;
}

