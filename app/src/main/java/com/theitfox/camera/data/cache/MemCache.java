package com.theitfox.camera.data.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by btquanto on 15/09/2016.
 * <p>
 * Memory cache utility using singleton design pattern
 */
public class MemCache {
    /**
     * MemCache entries expired in 15 minutes.
     * The entries are checked for expiration and removed on retrieval
     * <p>
     * TODO: Have a MemCacheService to periodically check for expired entries
     */
    private static final int EXPIRATION = 30; // 30 minutes

    private HashMap<String, Entry> entries;

    /**
     * Instantiates a new Mem cache.
     */
    public MemCache() {
        this.entries = new HashMap<>();
    }

    /**
     * Put a value into mem cache
     *
     * @param <T>   the type parameter
     * @param key   the key
     * @param value the value
     */
    public <T> void put(String key, T value) {
        entries.put(key, new Entry<>(value));
    }

    /**
     * Get a cached value
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the cached value, or null
     */
    public <T> T get(String key) {
        Entry entry = entries.get(key);
        if (entry != null) {
            if (entry.isExpired()) {
                entries.remove(key);
                entry = null;
            }
        }
        return (entry != null) ? (T) entry.getData() : null;
    }

    /**
     * Check if mem cache contains any unexpired entry for a given key
     *
     * @param key the key
     * @return whether mem cache contains an unexpired entry for `key`
     */
    public boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * Entry class for holding cached data and checking expiration
     */
    private class Entry<T> {
        private Date expiration;
        private T data;

        /**
         * Instantiates a new cached Entry.
         *
         * @param data the data for caching
         */
        public Entry(T data) {
            this.data = data;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, EXPIRATION);
            this.expiration = calendar.getTime();
        }

        /**
         * Get the cached data.
         *
         * @return the cached data
         */
        public T getData() {
            return data;
        }

        /**
         * Check if the cached entry has expired
         *
         * @return the boolean
         */
        public boolean isExpired() {
            return (new Date()).after(expiration);
        }
    }
}
