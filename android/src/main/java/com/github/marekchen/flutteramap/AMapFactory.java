package com.github.marekchen.flutteramap;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class AMapFactory extends PlatformViewFactory {

    private final AtomicInteger mActivityState;
    private final PluginRegistry.Registrar mPluginRegistrar;

    public AMapFactory(AtomicInteger state, PluginRegistry.Registrar registrar) {
        super(StandardMessageCodec.INSTANCE);
        mActivityState = state;
        mPluginRegistrar = registrar;
    }

    @Override
    public PlatformView create(Context context, int id, Object params) {
        final AMapBuilder builder = new AMapBuilder();
//        Convert.interpretGoogleMapOptions(params, builder);
        return builder.build(id, context, mActivityState, mPluginRegistrar);
    }
}
