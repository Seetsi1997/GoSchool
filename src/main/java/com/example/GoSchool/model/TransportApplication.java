package com.example.GoSchool.model;

import com.example.GoSchool.constant.ApplicationStatus;
import com.example.GoSchool.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "transport_application")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID applicationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private DriverRouteDetails route;

    @Column(name = "number_of_kids")
    private int numberOfKids;

    @Column(name = "message")
    private String message;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}

