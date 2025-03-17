package com.here.hellomap.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ReferencePoint {
    private Coordinate coordinate;
    private RefPointLineProperties refPointLineProperties;
    private RefPointPathProperties refPointPathProperties;

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @JsonProperty("lineProperties")
    public RefPointLineProperties getRefPointLineProperties() {
        return refPointLineProperties;
    }

    @JsonProperty("pathProperties")
    public RefPointPathProperties getRefPointPathProperties() {
        return refPointPathProperties;
    }

}

