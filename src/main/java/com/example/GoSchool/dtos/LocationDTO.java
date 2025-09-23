package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Province;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private UUID locationUUID;
    private String city;
    private String address;
    private String postalCode;
    private Province province;
}
