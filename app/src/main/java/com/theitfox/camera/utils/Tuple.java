package com.theitfox.camera.utils;

/**
 * Created by btquanto on 07/12/2016.
 */

public class Tuple {
    public final Object first;
    public final Object second;

    public Tuple(Object first, Object second) {
        this.first = first;
        this.second = second;
    }

    public <T> T getFirst() {
        return (T) first;
    }

    public <T> T getSecond() {
        return (T) second;
    }
}
