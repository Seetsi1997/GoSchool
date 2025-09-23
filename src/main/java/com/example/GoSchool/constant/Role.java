package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    DRIVER,
    ADMIN,
    PARENT;
    @JsonCreator
    public static Role fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Convert input to uppercase to match enum names
        return Role.valueOf(key.toUpperCase());
    }
}
