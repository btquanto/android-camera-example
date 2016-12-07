package com.theitfox.camera.application;

import android.app.Application;

import com.theitfox.camera.data.cache.MemCache;

/**
 * Created by btquanto on 05/08/2016.
 */
public class App extends Application {

    private AppState appState;

    @Override
    public void onCreate() {
        super.onCreate();
        this.appState = new AppState(new MemCache());
    }

    /**
     * Gets application state.
     *
     * @return the application state
     */
    public AppState getAppState() {
        return appState;
    }

    /**
     * The type Application state.
     */
    public static class AppState {

        private MemCache memCache;

        private AppState(MemCache memCache) {
            this.memCache = memCache;
        }

        /**
         * Gets mem cache.
         *
         * @return the mem cache
         */
        public MemCache getMemCache() {
            return memCache;
        }
    }
}
