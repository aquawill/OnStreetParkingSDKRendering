package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TpegOpenLR {
    public String binary;
    public String sideOfRoad;

    public String getBinary() {
        return binary;
    }

    public String getSideOfRoad() {
        return sideOfRoad;
    }
}