package com.here.hellomap.model;

public class RefPointPathProperties {
    private int lfrcnp;
    private int dnp;
    private boolean againstDrivingDirection;

    public int getLfrcnp() {
        return lfrcnp;
    }

    public void setLfrcnp(int lfrcnp) {
        this.lfrcnp = lfrcnp;
    }

    public int getDnp() {
        return dnp;
    }

    public void setDnp(int dnp) {
        this.dnp = dnp;
    }

    public boolean isAgainstDrivingDirection() {
        return againstDrivingDirection;
    }

    public void setAgainstDrivingDirection(boolean againstDrivingDirection) {
        this.againstDrivingDirection = againstDrivingDirection;
    }
}
