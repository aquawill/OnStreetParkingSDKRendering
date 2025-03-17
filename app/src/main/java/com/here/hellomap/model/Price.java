package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    private String maxStay;
    private List<Time> times;
    private List<Cost> cost;

    public String getMaxStay() {
        return maxStay;
    }

    public List<Cost> getCost() {
        return cost;
    }

    public List<Time> getTimes() {
        return times;
    }
}
