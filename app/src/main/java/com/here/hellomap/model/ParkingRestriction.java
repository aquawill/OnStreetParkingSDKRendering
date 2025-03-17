package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingRestriction {
    private String type;

    public String getType() {
        return type;
    }
}
