package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    CASH,
    EFT,
    ONLINE;

    @JsonCreator
    public static PaymentMethod fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Normalize input
        String normalized = key.trim().toUpperCase().replace(" ", "_");
                // Convert input to uppercase to match enum names
        return PaymentMethod.valueOf(normalized);
    }
}
