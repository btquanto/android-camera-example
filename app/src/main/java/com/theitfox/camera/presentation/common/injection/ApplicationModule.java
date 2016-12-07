package com.theitfox.camera.presentation.common.injection;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;

import com.theitfox.camera.application.App;
import com.theitfox.camera.data.cache.MemCache;
import com.theitfox.camera.presentation.utils.ObscuredSharedPreferences;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by btquanto on 11/10/2016.
 */
@Module
public class ApplicationModule {
    private Context context;

    /**
     * Instantiates a new Application module.
     *
     * @param context the context
     */
    public ApplicationModule(Context context) {
        this.context = context;
    }

    /**
     * Provide context context.
     *
     * @return the context
     */
    @Provides Context context() {
        return this.context;
    }

    /**
     * Application iris application.
     *
     * @param context the context
     * @return the iris application
     */
    @Provides App application(Context context) {
        return (App) context.getApplicationContext();
    }

    /**
     * Shared preferences shared preferences.
     *
     * @param context the context
     * @return the shared preferences
     */
    @Provides SharedPreferences sharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ObscuredSharedPreferences.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return new ObscuredSharedPreferences(context, sharedPreferences);
    }

    @Provides ContentResolver contentResolver(Context context) {
        return context.getContentResolver();
    }

    /**
     * Application state iris application . application state.
     *
     * @param application the application
     * @return the iris application . application state
     */
    @Provides App.AppState applicationState(App application) {
        return application.getAppState();
    }

    /**
     * Mem cache mem cache.
     *
     * @param appState the application state
     * @return the mem cache
     */
    @Provides MemCache memCache(App.AppState appState) {
        return appState.getMemCache();
    }

    /**
     * Provide execution thread scheduler.
     *
     * @return the scheduler
     */
    @Provides @Named("executionThread") Scheduler executionThread() {
        return Schedulers.newThread();
    }

    /**
     * Provide execution thread scheduler.
     *
     * @return the scheduler
     */
    @Provides @Named("ioThread") Scheduler ioThread() {
        return Schedulers.io();
    }

    /**
     * Provide post execution thread scheduler.
     *
     * @return the scheduler
     */
    @Provides @Named("postExecutionThread") Scheduler postExecutionThread() {
        return AndroidSchedulers.mainThread();
    }
}
