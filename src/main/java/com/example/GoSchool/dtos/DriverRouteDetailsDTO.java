package com.example.GoSchool.dtos;

import com.example.GoSchool.model.DriverRouteDetails;
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

    public DriverRouteDetailsDTO(DriverRouteDetails route) {
        this.id = route.getId();
        this.schoolName = route.getSchoolName();
        this.pickupTime = route.getPickupTime() != null ? route.getPickupTime().toString() : null;
        this.dropOffTime = route.getDropOffTime() != null ? route.getDropOffTime().toString() : null;
        this.monthlyFee = route.getMonthlyFee();
    }
}

