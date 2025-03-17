package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactInfo {
    public List<String> PHONE;
    public List<String> EMAIL;
    public List<String> WEB_ADDRESS;

    public List<String> getPHONE() {
        return PHONE;
    }

    public List<String> getEMAIL() {
        return EMAIL;
    }

    public List<String> getWEB_ADDRESS() {
        return WEB_ADDRESS;
    }
}