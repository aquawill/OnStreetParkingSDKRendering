package com.here.hellomap.model;

import java.util.List;

public class OpenLRLocation {
    private String type;
    private int positiveOffset;
    private int negativeOffset;
    private ReferencePoint firstReferencePoint;
    private List<ReferencePoint> intermediateReferencePoints; // 可選
    private ReferencePoint lastReferencePoint;

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPositiveOffset() {
        return positiveOffset;
    }

    public void setPositiveOffset(int positiveOffset) {
        this.positiveOffset = positiveOffset;
    }

    public int getNegativeOffset() {
        return negativeOffset;
    }

    public void setNegativeOffset(int negativeOffset) {
        this.negativeOffset = negativeOffset;
    }

    public ReferencePoint getFirstReferencePoint() {
        return firstReferencePoint;
    }

    public void setFirstReferencePoint(ReferencePoint firstReferencePoint) {
        this.firstReferencePoint = firstReferencePoint;
    }

    public List<ReferencePoint> getIntermediateReferencePoints() {
        return intermediateReferencePoints;
    }

    public void setIntermediateReferencePoints(List<ReferencePoint> intermediateReferencePoints) {
        this.intermediateReferencePoints = intermediateReferencePoints;
    }

    public ReferencePoint getLastReferencePoint() {
        return lastReferencePoint;
    }

    public void setLastReferencePoint(ReferencePoint lastReferencePoint) {
        this.lastReferencePoint = lastReferencePoint;
    }
}

