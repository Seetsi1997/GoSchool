package com.example.GoSchool.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "driver_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRouteDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Driver driver;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "pick_up", nullable = false)
    private LocalTime pickupTime;

    @Column(name = "drop_off", nullable = false)
    private LocalTime dropOffTime;

    @Column(name = "monthly_fee", nullable = false)
    private double monthlyFee;
}
