package com.theitfox.camera.utils;

import com.theitfox.camera.BuildConfig;

/**
 * Created by btquanto on 14/09/2016.
 */
public class Logger {
    private static final String DEFAULT_TAG = "IrisStudioLog";

    /**
     * V.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(tag, message);
        }
    }

    /**
     * V.
     *
     * @param message the message
     */
    public static void v(String message) {
        v(DEFAULT_TAG, message);
    }

    /**
     * .
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, message);
        }
    }

    /**
     * .
     *
     * @param message the message
     */
    public static void i(String message) {
        i(DEFAULT_TAG, message);
    }

    /**
     * D.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, message);
        }
    }

    /**
     * D.
     *
     * @param message the message
     */
    public static void d(String message) {
        d(DEFAULT_TAG, message);
    }

    /**
     * W.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag, message);
        }
    }

    /**
     * W.
     *
     * @param message the message
     */
    public static void w(String message) {
        w(DEFAULT_TAG, message);
    }

    /**
     * E.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, message);
        }
    }

    /**
     * E.
     *
     * @param message the message
     */
    public static void e(String message) {
        e(DEFAULT_TAG, message);
    }
}
