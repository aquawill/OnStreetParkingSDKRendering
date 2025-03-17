package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeRange {
    private String startTimeOfDay;
    private String endTimeOfDay;

    public String getStartTimeOfDay() {
        return startTimeOfDay;
    }

    public String getEndTimeOfDay() {
        return endTimeOfDay;
    }
}
