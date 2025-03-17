package com.here.hellomap.util;

import android.content.Context;
import com.here.hellomap.model.ParkingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsonParser {
    public static ParkingData parseParkingData(Context context, String jsonFilename) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = JsonUtil.loadJSONFromAsset(context, jsonFilename);
            return objectMapper.readValue(json, ParkingData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
