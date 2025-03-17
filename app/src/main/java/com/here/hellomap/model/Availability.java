package com.here.hellomap.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Availability {
    private String trend;
    private String lastUpdatedTimestamp;

    public String getTrend() {
        return trend;
    }

    public String getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }
}
