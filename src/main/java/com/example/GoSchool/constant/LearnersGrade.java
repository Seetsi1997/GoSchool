package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum LearnersGrade {
    @JsonProperty("Grade 8")
    GRADE_8,
    @JsonProperty("Grade 9")
    GRADE_9,
    @JsonProperty("Grade 10")
    GRADE_10,
    @JsonProperty("Grade 11")
    GRADE_11,
    @JsonProperty("Grade 12")
    GRADE_12;

    @JsonCreator
    public static LearnersGrade fromString(String key) {
        // Handle null input gracefully
        if (key == null) return null;
        // Normalize input
        String normalized = key.trim().toUpperCase().replace(" ", "_");
        // Convert input to uppercase to match enum names
        return LearnersGrade.valueOf(normalized);
    }
}
