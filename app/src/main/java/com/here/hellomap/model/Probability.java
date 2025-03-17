package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Probability {
    public int probability;

    public int getProbability() {
        return probability;
    }
}