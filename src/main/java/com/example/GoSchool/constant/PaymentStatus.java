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
        // Convert input to uppercase to match enum names
        return PaymentStatus.valueOf(key.toUpperCase());
    }
}
