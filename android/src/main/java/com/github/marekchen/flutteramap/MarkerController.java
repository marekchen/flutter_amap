package com.github.marekchen.flutteramap;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

public class MarkerController implements MarkerOptionsSink {

    private final Marker marker;
    private final OnMarkerTappedListener onTappedListener;
    private boolean consumeTapEvents;

    MarkerController(
            Marker marker, boolean consumeTapEvents, OnMarkerTappedListener onTappedListener) {
        this.marker = marker;
        this.consumeTapEvents = consumeTapEvents;
        this.onTappedListener = onTappedListener;
    }

    boolean onClick() {
        if (onTappedListener != null) {
            onTappedListener.onMarkerTapped(marker);
        }
        return consumeTapEvents;
    }

    void remove() {
        marker.remove();
    }

    @Override
    public void setAlpha(float alpha) {
        marker.setAlpha(alpha);
    }

    @Override
    public void setAnchor(float u, float v) {
        marker.setAnchor(u, v);
    }

    @Override
    public void setConsumeTapEvents(boolean consumeTapEvents) {
        this.consumeTapEvents = consumeTapEvents;
    }

    @Override
    public void setDraggable(boolean draggable) {
        marker.setDraggable(draggable);
    }

    @Override
    public void setFlat(boolean flat) {
        marker.setFlat(flat);
    }

    @Override
    public void setIcon(BitmapDescriptor bitmapDescriptor) {
        marker.setIcon(bitmapDescriptor);
    }

    @Override
    public void setInfoWindowAnchor(float u, float v) {
    }

    @Override
    public void setInfoWindowText(String title, String snippet) {
        marker.setTitle(title);
        marker.setSnippet(snippet);
    }

    @Override
    public void setPosition(LatLng position) {
        marker.setPosition(position);
    }

    @Override
    public void setRotation(float rotation) {
        marker.setRotateAngle(rotation);
    }

    @Override
    public void setVisible(boolean visible) {
        marker.setVisible(visible);
    }

    @Override
    public void setZIndex(float zIndex) {
        marker.setZIndex(zIndex);
    }
}
