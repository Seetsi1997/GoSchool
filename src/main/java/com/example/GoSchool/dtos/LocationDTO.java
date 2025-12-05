package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Province;
import com.example.GoSchool.model.Location;
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
    private String suburb;

    public Location toEntity() {
        Location loc = new Location();
        loc.setLocationUUID(this.locationUUID);
        loc.setCity(this.city);
        loc.setAddress(this.address);
        loc.setPostalCode(this.postalCode);
        loc.setProvince(this.province);
        loc.setSuburb(this.suburb);
        return loc;
    }


}
