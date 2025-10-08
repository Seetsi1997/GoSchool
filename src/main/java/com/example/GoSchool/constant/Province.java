package com.example.GoSchool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Province {
    @JsonProperty("GAUTENG")
    GAUTENG,
    @JsonProperty("EASTERN CAPE")
    EASTERN_CAPE,
    @JsonProperty("KWAZULU NATAL")
    KWAZULU_NATAL,
    @JsonProperty("FREE STATE")
    FREE_STATE,
    @JsonProperty("LIMPOPO")
    LIMPOPO,
    @JsonProperty("MPUMALANGA")
    MPUMALANGA,
    @JsonProperty("NORTH WEST")
    NORTH_WEST,
    @JsonProperty("NORTHERN CAPE")
    NORTHERN_CAPE,
    @JsonProperty("WESTERN CAPE")
    WESTERN_CAPE;
    @JsonCreator
    public static Province fromString(String key) {
        if (key == null) return null;
        for (Province p : Province.values()) {
            JsonProperty jp = null;
            try {
                jp = p.getClass().getField(p.name()).getAnnotation(JsonProperty.class);
            } catch (NoSuchFieldException ignored) {}
            String label = jp != null ? jp.value() : p.name();
            if (label.equalsIgnoreCase(key)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown province: " + key);
    }

}
