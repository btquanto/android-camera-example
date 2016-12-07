package com.theitfox.camera.utils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by btquanto on 07/12/2016.
 */

public class MapUtils {
    
    @Inject
    public MapUtils() {
    }

    public <K, V> Map<K, V> create(K[] keys, V[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("Number of keys and values mismatched!");
        }
        Map<K, V> map = new HashMap<K, V>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
}
