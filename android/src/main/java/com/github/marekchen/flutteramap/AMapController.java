package com.github.marekchen.flutteramap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformView;

import static com.github.marekchen.flutteramap.FlutterAmapPlugin.CREATED;
import static com.github.marekchen.flutteramap.FlutterAmapPlugin.PAUSED;
import static com.github.marekchen.flutteramap.FlutterAmapPlugin.RESUMED;

public class AMapController implements Application.ActivityLifecycleCallbacks,
        AMap.OnCameraChangeListener,
        AMap.OnInfoWindowClickListener,
        AMap.OnMarkerClickListener,
        AMapOptionsSink,
        MethodChannel.MethodCallHandler,
        AMap.OnMapLoadedListener,
        OnMarkerTappedListener,
        PlatformView {

    private final int id;
    private final AtomicInteger activityState;
    private final MethodChannel methodChannel;
    private final PluginRegistry.Registrar registrar;
    private final MapView mapView;
    private final Map<String, MarkerController> markers;
    private AMap aMap;
    private boolean trackCameraPosition = false;
    private boolean disposed = false;
    private final float density;
    private MethodChannel.Result mapReadyResult;

    AMapController(int id,
                   Context context,
                   AtomicInteger activityState,
                   PluginRegistry.Registrar registrar,
                   AMapOptions options) {
        this.id = id;
        this.activityState = activityState;
        this.registrar = registrar;
        this.mapView = new MapView(context, options);
        this.markers = new HashMap<>();
        this.density = context.getResources().getDisplayMetrics().density;
        methodChannel =
                new MethodChannel(registrar.messenger(), "plugins.marekchen.github.com/flutter_amap_" + id);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public View getView() {
        return mapView;
    }

    void init() {
        switch (activityState.get()) {
            case CREATED:
                mapView.onCreate(null);
                break;
            case RESUMED:
                mapView.onCreate(null);
                mapView.onResume();
                break;
            case PAUSED:
                mapView.onCreate(null);
                mapView.onResume();
                mapView.onPause();
                break;
            default:
                throw new IllegalArgumentException(
                        "Cannot interpret " + activityState.get() + " as an activity state");
        }
        registrar.activity().getApplication().registerActivityLifecycleCallbacks(this);
        mapView.getMap().setOnMapLoadedListener(this);
        // mapView.getMapAsync(this);
    }

    private void moveCamera(CameraUpdate cameraUpdate) {
        aMap.moveCamera(cameraUpdate);
    }

    private void animateCamera(CameraUpdate cameraUpdate) {
        aMap.animateCamera(cameraUpdate);
    }

    private CameraPosition getCameraPosition() {
        return trackCameraPosition ? aMap.getCameraPosition() : null;
    }

    private MarkerBuilder newMarkerBuilder() {
        return new MarkerBuilder(this);
    }

    Marker addMarker(MarkerOptions markerOptions, boolean consumesTapEvents) {
        final Marker marker = aMap.addMarker(markerOptions);
        markers.put(marker.getId(), new MarkerController(marker, consumesTapEvents, this));
        return marker;
    }

    private void removeMarker(String markerId) {
        final MarkerController markerController = markers.remove(markerId);
        if (markerController != null) {
            markerController.remove();
        }
    }

    private MarkerController marker(String markerId) {
        final MarkerController marker = markers.get(markerId);
        if (marker == null) {
            throw new IllegalArgumentException("Unknown marker: " + markerId);
        }
        return marker;
    }


    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "map#waitForMap":
                if (aMap != null) {
                    result.success(null);
                    return;
                }
                mapReadyResult = result;
                break;
            case "map#update": {
                Convert.interpretGoogleMapOptions(call.argument("options"), this);
                result.success(Convert.toJson(getCameraPosition()));
                break;
            }
            case "camera#move": {
                final CameraUpdate cameraUpdate =
                        Convert.toCameraUpdate(call.argument("cameraUpdate"), density);
                moveCamera(cameraUpdate);
                result.success(null);
                break;
            }
            case "camera#animate": {
                final CameraUpdate cameraUpdate =
                        Convert.toCameraUpdate(call.argument("cameraUpdate"), density);
                animateCamera(cameraUpdate);
                result.success(null);
                break;
            }
            case "marker#add": {
                final MarkerBuilder markerBuilder = newMarkerBuilder();
                Convert.interpretMarkerOptions(call.argument("options"), markerBuilder);
                final String markerId = markerBuilder.build();
                result.success(markerId);
                break;
            }
            case "marker#remove": {
                final String markerId = call.argument("marker");
                removeMarker(markerId);
                result.success(null);
                break;
            }
            case "marker#update": {
                final String markerId = call.argument("marker");
                final MarkerController marker = marker(markerId);
                Convert.interpretMarkerOptions(call.argument("options"), marker);
                result.success(null);
                break;
            }
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        final Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("marker", marker.getId());
        methodChannel.invokeMethod("infoWindow#onTap", arguments);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (!trackCameraPosition) {
            return;
        }
        final Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("position", Convert.toJson(cameraPosition));
        methodChannel.invokeMethod("camera#onMove", arguments);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (!trackCameraPosition) {
            return;
        }
        methodChannel.invokeMethod("camera#onIdle", Collections.singletonMap("map", id));
    }

    @Override
    public void onMarkerTapped(Marker marker) {
        final Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("marker", marker.getId());
        methodChannel.invokeMethod("marker#onTap", arguments);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final MarkerController markerController = markers.get(marker.getId());
        return (markerController != null && markerController.onClick());
    }

    @Override
    public void onMapLoaded() {
        aMap = mapView.getMap();
        aMap.setOnInfoWindowClickListener(this);
        if (mapReadyResult != null) {
            mapReadyResult.success(null);
            mapReadyResult = null;
        }
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMarkerClickListener(this);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        mapView.onDestroy();
        registrar.activity().getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (disposed) {
            return;
        }
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (disposed) {
            return;
        }
        mapView.onResume();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (disposed) {
            return;
        }
        mapView.onPause();
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if (disposed) {
            return;
        }
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (disposed) {
            return;
        }
        mapView.onDestroy();
    }

    // AMapOptionsSink methods
    @Override
    public void setCameraPosition(CameraPosition position) {
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    @Override
    public void setCameraTargetBounds(LatLngBounds bounds) {
        aMap.setMapStatusLimits(bounds);
    }

    @Override
    public void setCompassEnabled(boolean compassEnabled) {
        aMap.getUiSettings().setCompassEnabled(compassEnabled);
    }

    @Override
    public void setMapType(int mapType) {
        aMap.setMapType(mapType);
    }

    @Override
    public void setTrackCameraPosition(boolean trackCameraPosition) {
        this.trackCameraPosition = trackCameraPosition;
    }

    @Override
    public void setRotateGesturesEnabled(boolean rotateGesturesEnabled) {
        aMap.getUiSettings().setRotateGesturesEnabled(rotateGesturesEnabled);
    }

    @Override
    public void setScrollGesturesEnabled(boolean scrollGesturesEnabled) {
        aMap.getUiSettings().setScrollGesturesEnabled(scrollGesturesEnabled);
    }

    @Override
    public void setTiltGesturesEnabled(boolean tiltGesturesEnabled) {
        aMap.getUiSettings().setTiltGesturesEnabled(tiltGesturesEnabled);
    }

    @Override
    public void setMinMaxZoomPreference(Float min, Float max) {
        aMap.resetMinMaxZoomPreference();
        if (min != null) {
            aMap.setMinZoomLevel(min);
        }
        if (max != null) {
            aMap.setMaxZoomLevel(max);
        }
    }

    @Override
    public void setZoomGesturesEnabled(boolean zoomGesturesEnabled) {
        aMap.getUiSettings().setZoomGesturesEnabled(zoomGesturesEnabled);
    }
}
