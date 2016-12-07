package com.theitfox.camera.data.repositories;

import android.content.Context;

import com.theitfox.camera.data.cache.MemCache;

/**
 * Created by btquanto on 05/09/2016.
 */
public abstract class Repository {
    /**
     * A {@link MemCache} instance
     */
    protected MemCache memCache;

    /**
     * The Context.
     */
    protected Context context;

    /**
     * Instantiates a new Repository.
     *
     * @param context  the context
     * @param memCache the mem cache
     */
    public Repository(Context context, MemCache memCache) {
        this.context = context;
        this.memCache = memCache;
    }
}
