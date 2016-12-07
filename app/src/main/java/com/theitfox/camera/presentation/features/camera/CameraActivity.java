package com.theitfox.camera.presentation.features.camera;

import android.support.v4.app.Fragment;

import com.theitfox.camera.presentation.common.SingleFragmentActivity;
import com.theitfox.camera.presentation.features.camera.views.CameraFragment;

/**
 * Created by btquanto on 22/11/2016.
 */

public class CameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment onCreateFragment() {
        return new CameraFragment();
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }
}
