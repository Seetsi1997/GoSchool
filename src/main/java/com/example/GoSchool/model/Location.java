package com.example.GoSchool.model;

import com.example.GoSchool.constant.Province;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "location_uuid", updatable = false, nullable = false)
    private UUID locationUUID;

    @Column(name = "suburb", nullable = false)
    private String suburb;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street_address", nullable = false)
    private String address;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "province_name")
    private Province province;
}
