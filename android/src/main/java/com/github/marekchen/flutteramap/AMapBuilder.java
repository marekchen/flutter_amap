package com.github.marekchen.flutteramap;

import android.content.Context;

import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLngBounds;

import java.util.concurrent.atomic.AtomicInteger;

import io.flutter.plugin.common.PluginRegistry;

public class AMapBuilder implements AMapOptionsSink {
    private final AMapOptions options = new AMapOptions();
    private boolean trackCameraPosition = false;

    AMapController build(
            int id, Context context, AtomicInteger state, PluginRegistry.Registrar registrar) {
        final AMapController controller =
                new AMapController(id, context, state, registrar, options);
        controller.init();
        controller.setTrackCameraPosition(trackCameraPosition);
        return controller;
    }

    @Override
    public void setCameraPosition(CameraPosition position) {
        options.camera(position);
    }

    @Override
    public void setCompassEnabled(boolean compassEnabled) {
        options.compassEnabled(compassEnabled);
    }

    @Override
    public void setCameraTargetBounds(LatLngBounds bounds) {
    }

    @Override
    public void setMapType(int mapType) {
        options.mapType(mapType);
    }

    @Override
    public void setMinMaxZoomPreference(Float min, Float max) {
    }

    @Override
    public void setTrackCameraPosition(boolean trackCameraPosition) {
        this.trackCameraPosition = trackCameraPosition;
    }

    @Override
    public void setRotateGesturesEnabled(boolean rotateGesturesEnabled) {
        options.rotateGesturesEnabled(rotateGesturesEnabled);
    }

    @Override
    public void setScrollGesturesEnabled(boolean scrollGesturesEnabled) {
        options.scrollGesturesEnabled(scrollGesturesEnabled);
    }

    @Override
    public void setTiltGesturesEnabled(boolean tiltGesturesEnabled) {
        options.tiltGesturesEnabled(tiltGesturesEnabled);
    }

    @Override
    public void setZoomGesturesEnabled(boolean zoomGesturesEnabled) {
        options.zoomGesturesEnabled(zoomGesturesEnabled);
    }
}

