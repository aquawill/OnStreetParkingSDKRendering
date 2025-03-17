package com.here.hellomap.util;

import com.here.sdk.core.GeoCoordinates;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.linearref.LengthIndexedLine;

import java.util.ArrayList;
import java.util.List;

public class RouteGeometryTrimmer {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static List<GeoCoordinates> trimRouteGeometry(List<GeoCoordinates> routeGeometry, int positiveOffset, int negativeOffset) {
        if (routeGeometry.size() < 2) {
            return routeGeometry; // 確保至少有兩個點
        }

        // 轉換成 JTS LineString
        Coordinate[] coords = routeGeometry.stream()
                .map(p -> new Coordinate(p.longitude, p.latitude)) // JTS 使用 (x, y) = (lon, lat)
                .toArray(Coordinate[]::new);

        LineString line = geometryFactory.createLineString(coords);
        LengthIndexedLine indexedLine = new LengthIndexedLine(line);

        // **檢查 offset 是否超過路線長度**
        double totalLength = line.getLength();
        double startOffset = Math.min(positiveOffset, totalLength - 1);
        double endOffset = Math.max(totalLength - negativeOffset, 1);

        // **確保至少保留一個點**
        if (startOffset >= endOffset) {
            startOffset = 0;
            endOffset = totalLength;
        }

        // **取得裁剪後的線**
        LineString trimmedLine = (LineString) indexedLine.extractLine(startOffset, endOffset);

        // 轉回 `GeoCoordinates`
        List<GeoCoordinates> trimmedCoordinates = new ArrayList<>();
        for (Coordinate c : trimmedLine.getCoordinates()) {
            trimmedCoordinates.add(new GeoCoordinates(c.y, c.x)); // JTS 使用 (x=lon, y=lat)
        }

        return trimmedCoordinates;
    }
}
