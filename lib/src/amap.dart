// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

part of flutter_amap_3d;

typedef void MapCreatedCallback(AMapController controller);

class AMap extends StatefulWidget {
  AMap({
    @required this.onMapCreated,
    AMapOptions options,
    this.gestureRecognizers = const <OneSequenceGestureRecognizer>[],
  })  : assert(gestureRecognizers != null),
        this.options = AMapOptions.defaultOptions.copyWith(options);

  final MapCreatedCallback onMapCreated;

  final AMapOptions options;

  /// Which gestures should be consumed by the map.
  ///
  /// It is possible for other gesture recognizers to be competing with the map on pointer
  /// events, e.g if the map is inside a [ListView] the [ListView] will want to handle
  /// vertical drags. The map will claim gestures that are recognized by any of the
  /// recognizers on this list.
  ///
  /// When this list is empty, the map will only handle pointer events for gestures that
  /// were not claimed by any other gesture recognizer.
  final List<OneSequenceGestureRecognizer> gestureRecognizers;

  @override
  State createState() => _AMapState();
}

class _AMapState extends State<AMap> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'plugins.marekchen.github.com/flutter_amap',
        onPlatformViewCreated: onPlatformViewCreated,
        gestureRecognizers: widget.gestureRecognizers,
        creationParams: widget.options._toJson(),
        creationParamsCodec: const StandardMessageCodec(),
      );
    }

    return Text(
        '$defaultTargetPlatform is not yet supported by the maps plugin');
  }

  Future<void> onPlatformViewCreated(int id) async {
    final AMapController controller =
        await AMapController.init(id, widget.options);
    widget.onMapCreated(controller);
  }
}
