/*
 * Copyright (C) 2019-2025 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.hellomap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.foobar.OpenLRDecoder;

import com.here.hellomap.model.OpenLRLocation;
import com.here.hellomap.model.ParkingData;
import com.here.hellomap.model.ParkingSegment;
import com.here.hellomap.model.ReferencePoint;
import com.here.hellomap.util.JsonParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.here.hellomap.PermissionsRequestor.ResultListener;
import com.here.hellomap.util.OpenLRConverter;
import com.here.hellomap.util.PolylineOffsetProcessor;
import com.here.hellomap.util.RouteGeometryTrimmer;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoBox;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoOrientationUpdate;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.Rectangle2D;
import com.here.sdk.core.Size2D;
import com.here.sdk.core.engine.AuthenticationMode;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapview.LineCap;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapMeasureDependentRenderSize;
import com.here.sdk.mapview.MapPickResult;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.MapViewBase;
import com.here.sdk.mapview.PickMapItemsResult;
import com.here.sdk.mapview.RenderSize;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.OfflineRoutingEngine;
import com.here.sdk.routing.OptimizationMode;
import com.here.sdk.routing.PedestrianOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final BlockingQueue<ParkingSegment> routingQueue = new LinkedBlockingQueue<>();
    private final ExecutorService routingExecutor = Executors.newSingleThreadExecutor();

    private PermissionsRequestor permissionsRequestor;
    private MapView mapView;
    private Context context;
    private OfflineRoutingEngine offlineRoutingEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Usually, you need to initialize the HERE SDK only once during the lifetime of an application.
        // Before creating a MapView instance please make sure that the HERE SDK is initialized.
        initializeHERESDK();

        setContentView(R.layout.activity_main);
        // Get a MapView instance from layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.setOnReadyListener(new MapView.OnReadyListener() {
            @Override
            public void onMapViewReady() {
                // This will be called each time after this activity is resumed.
                // It will not be called before the first map scene was loaded.
                // Any code that requires map data may not work as expected until this event is received.
                Log.d(TAG, "HERE Rendering Engine attached.");
            }
        });

        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(@NonNull Point2D point2D) {
                pickMapPolyline(point2D);
            }
        });

        // Note that for this app handling of permissions is optional as no sensitive permissions
        // are required.
        handleAndroidPermissions();
    }

    private void pickMapPolyline(Point2D touchPoint) {
        Point2D originInPixels = new Point2D(touchPoint.x, touchPoint.y);
        Size2D sizeInPixels = new Size2D(20, 20);
        Rectangle2D rectangle = new Rectangle2D(originInPixels, sizeInPixels);

        // Creates a list of map content type from which the results will be picked.
        // The content type values can be MAP_CONTENT, MAP_ITEMS and CUSTOM_LAYER_DATA.
        ArrayList<MapScene.MapPickFilter.ContentType> contentTypesToPickFrom = new ArrayList<>();

        // MAP_CONTENT is used when picking embedded carto POIs, traffic incidents, vehicle restriction etc.
        // MAP_ITEMS is used when picking map items such as MapMarker, MapPolyline, MapPolygon etc.
        // Currently we need map markers so adding the MAP_ITEMS filter.
        contentTypesToPickFrom.add(MapScene.MapPickFilter.ContentType.MAP_ITEMS);
        MapScene.MapPickFilter filter = new MapScene.MapPickFilter(contentTypesToPickFrom);

        // If you do not want to specify any filter you can pass filter as NULL and all of the pickable contents will be picked.
        mapView.pick(filter, rectangle, new MapViewBase.MapPickCallback() {
            @Override
            public void onPickMap(@Nullable MapPickResult mapPickResult) {
                if (mapPickResult == null) {
                    // An error occurred while performing the pick operation.
                    return;
                }
                PickMapItemsResult pickMapItemsResult = mapPickResult.getMapItems();

                // Note that 3D map markers can't be picked yet. Only marker, polygon and polyline map items are pickable.
                List<MapPolyline> mapPolylineList = pickMapItemsResult.getPolylines();
                int listSize = mapPolylineList.size();
                if (listSize == 0) {
                    return;
                }
                MapPolyline pickedMapPolyline = mapPolylineList.get(0);
                Log.d("PARKING", Objects.requireNonNull(pickedMapPolyline.getMetadata()).getString("id"));

            }
        });
    }

    private void initializeHERESDK() {
        // Set your credentials for the HERE SDK.
        String accessKeyID = "";
        String accessKeySecret = "";
        AuthenticationMode authenticationMode = AuthenticationMode.withKeySecret(accessKeyID, accessKeySecret);
        SDKOptions options = new SDKOptions(authenticationMode);

        try {
            context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new ResultListener() {

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private MapPolyline createMapPolyline(GeoPolyline geoPolyline, int probability) {
        float widthInPixels = 16;

        probability = Math.max(-1, Math.min(100, probability));

        Color lineColor;
        if (probability >= 80) {
            lineColor = Color.valueOf(0, 1.0f, 0, 0.8f);
        } else if (probability >= 50) {
            lineColor = Color.valueOf(1.0f, 1.0f, 0, 0.8f);
        } else if (probability >= 30) {
            lineColor = Color.valueOf(1.0f, 0.65f, 0, 0.8f);
        } else if (probability >= 0) {
            lineColor = Color.valueOf(1.0f, 0, 0, 0.8f);
        } else {
            lineColor = Color.valueOf(0, 0, 0, 0.5f);
        }

        MapPolyline mapPolyline = null;
        try {
            mapPolyline = new MapPolyline(geoPolyline, new MapPolyline.SolidRepresentation(new MapMeasureDependentRenderSize(RenderSize.Unit.PIXELS, widthInPixels), lineColor, LineCap.ROUND));
        } catch (MapPolyline.Representation.InstantiationException e) {
            Log.e("MapPolyline Representation Exception:", e.error.name());
        } catch (MapMeasureDependentRenderSize.InstantiationException e) {
            Log.e("MapMeasureDependentRenderSize Exception:", e.error.name());
        }

        return mapPolyline;
    }

    private void processNextRouting() {
        ParkingSegment segment = routingQueue.poll();  // 取出下一個 Routing 任務
        if (segment == null) return;  // 若沒有任務，則停止

        int probability = (segment.getProbability() != null) ? segment.getProbability().get(0).probability : -1;
        if (segment.getTpegOpenLR() != null) {
            String openLrBinary = segment.getTpegOpenLR().binary;
            String sideOfRoad = segment.getTpegOpenLR().sideOfRoad;
            Map<String, Object> decodedResult = OpenLRDecoder.decodeOpenLR(openLrBinary, false, false);
            OpenLRLocation openLRLocation = OpenLRConverter.convertToOpenLRLocation(decodedResult);

            int parkingSegmentPositiveOffset = openLRLocation.getPositiveOffset();
            int parkingSegmentNegativeOffset = openLRLocation.getNegativeOffset();

            List<Waypoint> parkingSegmentPointList = new ArrayList<>();
            ReferencePoint firstRefPoint = openLRLocation.getFirstReferencePoint();
            parkingSegmentPointList.add(new Waypoint(new GeoCoordinates(firstRefPoint.getCoordinate().getLat(), firstRefPoint.getCoordinate().getLon())));

            if (openLRLocation.getIntermediateReferencePoints() != null) {
                for (ReferencePoint ref : openLRLocation.getIntermediateReferencePoints()) {
                    parkingSegmentPointList.add(new Waypoint(new GeoCoordinates(ref.getCoordinate().getLat(), ref.getCoordinate().getLon())));
                }
            }

            ReferencePoint lastRefPoint = openLRLocation.getLastReferencePoint();
            parkingSegmentPointList.add(new Waypoint(new GeoCoordinates(lastRefPoint.getCoordinate().getLat(), lastRefPoint.getCoordinate().getLon())));

            PedestrianOptions pedestrianOptions = new PedestrianOptions();
            pedestrianOptions.routeOptions.alternatives = 0;
            pedestrianOptions.routeOptions.optimizationMode = OptimizationMode.SHORTEST;

            offlineRoutingEngine.calculateRoute(parkingSegmentPointList, pedestrianOptions, new CalculateRouteCallback() {
                @Override
                public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> list) {
                    if (routingError == null && list != null) {
                        List<GeoCoordinates> routeGeometry = list.get(0).getGeometry().vertices;
                        List<GeoCoordinates> trimmedGeometry = RouteGeometryTrimmer.trimRouteGeometry(routeGeometry, parkingSegmentPositiveOffset, parkingSegmentNegativeOffset);
                        List<GeoCoordinates> offsetGeometry = PolylineOffsetProcessor.offsetPolyline(trimmedGeometry, sideOfRoad);

                        try {
                            GeoPolyline geoPolyline = new GeoPolyline(offsetGeometry);
                            MapPolyline mapPolyline = createMapPolyline(geoPolyline, probability);
                            Metadata mapPolylineMetadata = new Metadata();
                            mapPolylineMetadata.setString("id", segment.getId());
                            mapPolylineMetadata.setInteger("capacity", segment.getCapacity());
                            mapPolylineMetadata.setString("olr", segment.getTpegOpenLR().binary);
                            mapPolylineMetadata.setString("side", segment.getTpegOpenLR().sideOfRoad);
                            mapPolyline.setMetadata(mapPolylineMetadata);
                            mapView.getMapScene().addMapPolyline(mapPolyline);
                        } catch (InstantiationErrorException e) {
                            Log.e(TAG, "Failed to create polyline", e);
                        }
                    } else {
                        Log.e(TAG, "Route calculation failed: " + (routingError != null ? routingError.toString() : "Unknown error"));
                    }

                    processNextRouting();
                }
            });
        }
    }


    private void loadMapScene() {
        // The camera can be configured before or after a scene is loaded.
        double distanceInMeters = 1000 * 10;
        MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, distanceInMeters);
        GeoBox mapViewGeoBox = new GeoBox(new GeoCoordinates(52.50924410890286, 13.382594687430482), new GeoCoordinates(52.51700240896973, 13.394372921958052));
        GeoOrientationUpdate geoOrientationUpdate = new GeoOrientationUpdate(0d, 0d);
        mapView.getCamera().lookAt(mapViewGeoBox, geoOrientationUpdate);
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError != null) {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                } else {
                    Log.d(TAG, "Loading map succeeded!");

                    try {
                        offlineRoutingEngine = new OfflineRoutingEngine(SDKNativeEngine.getSharedInstance());
                    } catch (InstantiationErrorException e) {
                        Log.e(TAG, "Failed to initialize OfflineRoutingEngine", e);
                        return;
                    }
                    // Parse JSON
                    ParkingData parkingData = JsonParser.parseParkingData(context, "parking_data.json");

                    if (parkingData != null) {
                        routingQueue.addAll(parkingData.getParkingSegments());
                        processNextRouting();
                    } else {
                        Log.e("PARKING", "解析 JSON 失敗！");
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        disposeHERESDK();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void disposeHERESDK() {
        // Free HERE SDK resources before the application shuts down.
        // Usually, this should be called only on application termination.
        // Afterwards, the HERE SDK is no longer usable unless it is initialized again.
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            // For safety reasons, we explicitly set the shared instance to null to avoid situations,
            // where a disposed instance is accidentally reused.
            SDKNativeEngine.setSharedInstance(null);
        }
    }
}
