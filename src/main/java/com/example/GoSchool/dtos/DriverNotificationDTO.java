package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Actor;
import com.example.GoSchool.constant.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverNotificationDTO {

    private UUID id;
    private String message;
    private boolean seen;
    private LocalDateTime createdAt;
    private UUID applicationId;
    private UUID driverUUID;
    private ApplicationStatus applicationStatus;
    private Actor actor;
    private String parentName;
    private String studentName;
    private String address;
    private String suburb;
    private String city;
    private String province;
    private String postalCode;
    private String schoolName;
}
