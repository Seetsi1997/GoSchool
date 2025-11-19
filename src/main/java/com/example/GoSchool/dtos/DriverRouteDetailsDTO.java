package com.example.GoSchool.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRouteDetailsDTO {
    private UUID id;
    private String schoolName;
    private String pickupTime;
    private String dropOffTime;
    private double monthlyFee;
}
