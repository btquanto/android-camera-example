package com.theitfox.camera.presentation.common.mvp;

import android.content.Context;

/**
 * Created by btquanto on 16/08/2016.
 * <p>
 * The BaseView that all MVP Views would extends from
 */
public interface BaseView {
    /**
     * All views are expected to implement this method
     * If views are fragments, it should have already been implemented
     *
     * @return the context
     */
    Context getContext();

}
