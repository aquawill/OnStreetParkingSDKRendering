package com.here.hellomap.util;

import com.here.sdk.core.GeoCoordinates;

import java.util.ArrayList;
import java.util.List;

public class BearingCalculator {
    public static List<Double> calculateBearings(List<GeoCoordinates> routeGeometry) {
        List<Double> bearings = new ArrayList<>();

        for (int i = 0; i < routeGeometry.size() - 1; i++) {
            GeoCoordinates point1 = routeGeometry.get(i);
            GeoCoordinates point2 = routeGeometry.get(i + 1);
            double bearing = computeBearing(point1, point2);
            bearings.add(bearing);
        }

        // 對於最後一個點，使用倒數第二個點的 bearing
        if (!bearings.isEmpty()) {
            bearings.add(bearings.get(bearings.size() - 1));
        }

        return bearings;
    }

    private static double computeBearing(GeoCoordinates start, GeoCoordinates end) {
        double lat1 = Math.toRadians(start.latitude);
        double lon1 = Math.toRadians(start.longitude);
        double lat2 = Math.toRadians(end.latitude);
        double lon2 = Math.toRadians(end.longitude);

        double dLon = lon2 - lon1;

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearing = Math.toDegrees(Math.atan2(y, x));

        return (bearing + 360) % 360; // 確保角度在 0-360 之間
    }
}
