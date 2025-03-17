package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Time {
    private int days;
    private TimeRange timeRange;

    public int getDays() {
        return days;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }
}
