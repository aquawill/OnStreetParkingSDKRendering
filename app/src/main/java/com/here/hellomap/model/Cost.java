package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cost {
    private String duration;
    private double amount;
    private String type;

    public String getDuration() {
        return duration;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
