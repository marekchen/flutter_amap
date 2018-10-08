// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:flutter/material.dart';
import 'package:flutter_amap_3d/flutter_amap_3d.dart';

import 'page.dart';

final LatLngBounds sydneyBounds = LatLngBounds(
  southwest: const LatLng(-34.022631, 150.620685),
  northeast: const LatLng(-33.571835, 151.325952),
);

class MapUiPage extends Page {
  MapUiPage() : super(const Icon(Icons.map), 'User interface');

  @override
  Widget build(BuildContext context) {
    return const MapUiBody();
  }
}

class MapUiBody extends StatefulWidget {
  const MapUiBody();

  @override
  State<StatefulWidget> createState() => MapUiBodyState();
}

class MapUiBodyState extends State<MapUiBody> {
  MapUiBodyState();

  AMapController mapController;
  CameraPosition _position;
  AMapOptions _options = AMapOptions(
    cameraPosition: const CameraPosition(
      target: LatLng(-33.852, 151.211),
      zoom: 11.0,
    ),
    trackCameraPosition: true,
    compassEnabled: true,
  );
  bool _isMoving = false;

  @override
  void initState() {
    super.initState();
  }

  void _onMapChanged() {
    setState(() {
      _extractMapInfo();
    });
  }

  void _extractMapInfo() {
    _options = mapController.options;
    _position = mapController.cameraPosition;
    _isMoving = mapController.isCameraMoving;
  }

  @override
  void dispose() {
    mapController.removeListener(_onMapChanged);
    super.dispose();
  }

  Widget _compassToggler() {
    return FlatButton(
      child: Text('${_options.compassEnabled ? 'disable' : 'enable'} compass'),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(compassEnabled: !_options.compassEnabled),
        );
      },
    );
  }

  Widget _latLngBoundsToggler() {
    return FlatButton(
      child: Text(
        _options.cameraTargetBounds.bounds == null
            ? 'bound camera target'
            : 'release camera target',
      ),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(
            cameraTargetBounds: _options.cameraTargetBounds.bounds == null
                ? CameraTargetBounds(sydneyBounds)
                : CameraTargetBounds.unbounded,
          ),
        );
      },
    );
  }

  Widget _zoomBoundsToggler() {
    return FlatButton(
      child: Text(_options.minMaxZoomPreference.minZoom == null
          ? 'bound zoom'
          : 'release zoom'),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(
            minMaxZoomPreference: _options.minMaxZoomPreference.minZoom == null
                ? const MinMaxZoomPreference(12.0, 16.0)
                : MinMaxZoomPreference.unbounded,
          ),
        );
      },
    );
  }

  Widget _mapTypeCycler() {
    final MapType nextType =
        MapType.values[(_options.mapType.index + 1) % MapType.values.length];
    return FlatButton(
      child: Text('change map type to $nextType'),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(mapType: nextType),
        );
      },
    );
  }

  Widget _rotateToggler() {
    return FlatButton(
      child: Text(
          '${_options.rotateGesturesEnabled ? 'disable' : 'enable'} rotate'),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(
            rotateGesturesEnabled: !_options.rotateGesturesEnabled,
          ),
        );
      },
    );
  }

  Widget _scrollToggler() {
    return FlatButton(
      child: Text(
          '${_options.scrollGesturesEnabled ? 'disable' : 'enable'} scroll'),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(
            scrollGesturesEnabled: !_options.scrollGesturesEnabled,
          ),
        );
      },
    );
  }

  Widget _tiltToggler() {
    return FlatButton(
      child:
          Text('${_options.tiltGesturesEnabled ? 'disable' : 'enable'} tilt'),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(
            tiltGesturesEnabled: !_options.tiltGesturesEnabled,
          ),
        );
      },
    );
  }

  Widget _zoomToggler() {
    return FlatButton(
      child:
          Text('${_options.zoomGesturesEnabled ? 'disable' : 'enable'} zoom'),
      onPressed: () {
        mapController.updateMapOptions(
          AMapOptions(
            zoomGesturesEnabled: !_options.zoomGesturesEnabled,
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final List<Widget> columnChildren = <Widget>[
      Padding(
        padding: const EdgeInsets.all(10.0),
        child: Center(
          child: SizedBox(
            width: 300.0,
            height: 200.0,
            child: AMap(
              onMapCreated: onMapCreated,
              options: AMapOptions(
                cameraPosition: const CameraPosition(
                  target: LatLng(-33.852, 151.211),
                  zoom: 11.0,
                ),
                trackCameraPosition: true,
              ),
            ),
          ),
        ),
      ),
    ];

    if (mapController != null) {
      columnChildren.add(
        Expanded(
          child: ListView(
            children: <Widget>[
              Text('camera bearing: ${_position.bearing}'),
              Text(
                  'camera target: ${_position.target.latitude.toStringAsFixed(4)},'
                  '${_position.target.longitude.toStringAsFixed(4)}'),
              Text('camera zoom: ${_position.zoom}'),
              Text('camera tilt: ${_position.tilt}'),
              Text(_isMoving ? '(Camera moving)' : '(Camera idle)'),
              _compassToggler(),
              _latLngBoundsToggler(),
              _mapTypeCycler(),
              _zoomBoundsToggler(),
              _rotateToggler(),
              _scrollToggler(),
              _tiltToggler(),
              _zoomToggler(),
            ],
          ),
        ),
      );
    }
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: columnChildren,
    );
  }

  void onMapCreated(AMapController controller) {
    mapController = controller;
    mapController.addListener(_onMapChanged);
    _extractMapInfo();
    setState(() {});
  }
}
