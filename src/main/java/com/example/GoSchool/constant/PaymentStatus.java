package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentStatus {
    PAID,
    UNPAID,
    PENDING;

    @JsonCreator
    public static PaymentStatus fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Normalize input
        String normalized = key.trim().toUpperCase().replace(" ", "_");
        // Convert input to uppercase to match enum names
        return PaymentStatus.valueOf(normalized);
    }
}
