package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Actor {
    DRIVER,
    ADMIN;

    @JsonCreator
    public static Actor fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Normalize input
        String normalized = key.trim().toUpperCase().replace(" ", "_");
        // Convert input to uppercase to match enum names
        return Actor.valueOf(normalized);
    }
}
