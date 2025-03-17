package com.here.hellomap.model;

public class RefPointLineProperties {
    private int bearing;
    private double bearingDegrees;
    private int frc;
    private int fow;
    private String fowDescription;

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public double getBearingDegrees() {
        return bearingDegrees;
    }

    public void setBearingDegrees(double bearingDegrees) {
        this.bearingDegrees = bearingDegrees;
    }

    public int getFrc() {
        return frc;
    }

    public void setFrc(int frc) {
        this.frc = frc;
    }

    public int getFow() {
        return fow;
    }

    public void setFow(int fow) {
        this.fow = fow;
    }

    public String getFowDescription() {
        return fowDescription;
    }

    public void setFowDescription(String fowDescription) {
        this.fowDescription = fowDescription;
    }
}
