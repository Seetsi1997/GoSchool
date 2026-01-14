package com.example.GoSchool.model;

import com.example.GoSchool.constant.Actor;
import com.example.GoSchool.constant.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "driver_notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private Driver driver;

    @ManyToOne(optional = false)
    private TransportApplication application;

    @Column(name="message", nullable = false)
    private String message;

    @Column(name="seen", nullable = false)
    private boolean seen = false;

    @Column(name="create_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @Column(name="actor", nullable = false)
    @Enumerated(EnumType.STRING)
    private Actor actor;

}
