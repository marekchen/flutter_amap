package com.github.marekchen.flutteramap;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;

interface MarkerOptionsSink {
    void setAlpha(float alpha);

    void setAnchor(float u, float v);

    void setConsumeTapEvents(boolean consumesTapEvents);

    void setDraggable(boolean draggable);

    void setFlat(boolean flat);

    void setIcon(BitmapDescriptor bitmapDescriptor);

    void setInfoWindowAnchor(float u, float v);

    void setInfoWindowText(String title, String snippet);

    void setPosition(LatLng position);

    void setRotation(float rotation);

    void setVisible(boolean visible);

    void setZIndex(float zIndex);
}
