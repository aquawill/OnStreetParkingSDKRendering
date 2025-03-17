package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingData {
    public List<ParkingSegment> getParkingSegments() {
        return parkingSegments;
    }

    private List<ParkingSegment> parkingSegments;


}
