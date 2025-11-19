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
        // Normalize input
        String normalized = key.trim().toUpperCase().replace(" ", "_");
        // Convert input to uppercase to match enum names
        return Role.valueOf(normalized);
    }
}
