package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportApplicationDTO {
    private UUID parentId;
    private UUID routeId;
    private int numberOfKids;
    private String message;
}