package com.here.hellomap.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.here.hellomap.model.OpenLRLocation;

import java.util.Map;

public class OpenLRConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static OpenLRLocation convertToOpenLRLocation(Map<String, Object> map) {
        return objectMapper.convertValue(map, OpenLRLocation.class);
    }
}