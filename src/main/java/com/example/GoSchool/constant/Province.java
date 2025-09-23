package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Province {
    GAUTENG,
    EASTERN_CAPE,
    KWAZULU_NATAL,
    FREE_STATE,
    LIMPOPO,
    MPUMALANGA,
    NORTH_WEST,
    NORTHERN_CAPE,
    WESTERN_CAPE;
    @JsonCreator
    public static Province fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Convert input to uppercase to match enum names
        return Province.valueOf(key.toUpperCase());
    }
}
