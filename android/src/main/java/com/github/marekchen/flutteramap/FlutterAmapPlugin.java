package com.github.marekchen.flutteramap;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.concurrent.atomic.AtomicInteger;

import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterAmapPlugin
 */
public class FlutterAmapPlugin implements Application.ActivityLifecycleCallbacks {

    static final int CREATED = 1;
    static final int RESUMED = 3;
    static final int PAUSED = 4;
    static final int DESTROYED = 6;
    private final AtomicInteger state = new AtomicInteger(0);

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final FlutterAmapPlugin plugin = new FlutterAmapPlugin();
        registrar.activity().getApplication().registerActivityLifecycleCallbacks(plugin);
        registrar
                .platformViewRegistry()
                .registerViewFactory(
                        "plugins.marekchen.github.com/flutter_amap", new AMapFactory(plugin.state, registrar));
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        state.set(CREATED);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        state.set(RESUMED);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        state.set(PAUSED);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        state.set(DESTROYED);
    }
}
