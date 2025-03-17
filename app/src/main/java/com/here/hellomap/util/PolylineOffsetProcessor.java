package com.here.hellomap.util;

import com.here.sdk.core.GeoCoordinates;
import java.util.ArrayList;
import java.util.List;

public class PolylineOffsetProcessor {
    private static final double OFFSET_DISTANCE_METERS = 3.0;

    public static List<GeoCoordinates> offsetPolyline(List<GeoCoordinates> originalPolyline, String sideOfRoad) {
        if (originalPolyline.size() < 2) {
            return originalPolyline;
        }

        // 計算 `bearing`
        double bearing = computeBearing(originalPolyline.get(0), originalPolyline.get(originalPolyline.size() - 1));


        double offsetBearing = sideOfRoad.equalsIgnoreCase("LEFT") ? bearing + 90 : bearing - 90;
        offsetBearing = normalizeBearing(offsetBearing);

        List<GeoCoordinates> offsetPolyline = new ArrayList<>();
        for (GeoCoordinates point : originalPolyline) {
            offsetPolyline.add(calculateOffset(point, offsetBearing, OFFSET_DISTANCE_METERS));
        }

        return offsetPolyline;
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

        return (bearing + 360) % 360;
    }

    private static GeoCoordinates calculateOffset(GeoCoordinates point, double bearing, double distanceMeters) {
        final double R = 6371000;
        double latRad = Math.toRadians(point.latitude);
        double lonRad = Math.toRadians(point.longitude);
        double bearingRad = Math.toRadians(bearing);

        double newLat = Math.asin(Math.sin(latRad) * Math.cos(distanceMeters / R) +
                Math.cos(latRad) * Math.sin(distanceMeters / R) * Math.cos(bearingRad));

        double newLon = lonRad + Math.atan2(Math.sin(bearingRad) * Math.sin(distanceMeters / R) * Math.cos(latRad),
                Math.cos(distanceMeters / R) - Math.sin(latRad) * Math.sin(newLat));

        return new GeoCoordinates(Math.toDegrees(newLat), Math.toDegrees(newLon));
    }

    private static double normalizeBearing(double bearing) {
        while (bearing < 0) {
            bearing += 360;
        }
        while (bearing >= 360) {
            bearing -= 360;
        }
        return bearing;
    }
}
