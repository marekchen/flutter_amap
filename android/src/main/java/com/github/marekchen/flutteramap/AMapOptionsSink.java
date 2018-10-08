package com.github.marekchen.flutteramap;

import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLngBounds;

interface AMapOptionsSink {
    void setCameraPosition(CameraPosition position);

    void setCameraTargetBounds(LatLngBounds bounds);

    void setCompassEnabled(boolean compassEnabled);

    void setMapType(int mapType);

    void setMinMaxZoomPreference(Float min, Float max);

    void setRotateGesturesEnabled(boolean rotateGesturesEnabled);

    void setScrollGesturesEnabled(boolean scrollGesturesEnabled);

    void setTiltGesturesEnabled(boolean tiltGesturesEnabled);

    void setTrackCameraPosition(boolean trackCameraPosition);

    void setZoomGesturesEnabled(boolean zoomGesturesEnabled);
}