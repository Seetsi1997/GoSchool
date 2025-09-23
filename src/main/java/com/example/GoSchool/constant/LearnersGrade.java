package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LearnersGrade {
    GRADE_8,
    GRADE_9,
    GRADE_10,
    GRADE_11,
    GRADE_12;

    @JsonCreator
    public static LearnersGrade fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Convert input to uppercase to match enum names
        return LearnersGrade.valueOf(key.toUpperCase());
    }
}
