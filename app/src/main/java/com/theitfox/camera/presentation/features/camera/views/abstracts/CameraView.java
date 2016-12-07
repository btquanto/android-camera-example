package com.theitfox.camera.presentation.features.camera.views.abstracts;

import android.graphics.Bitmap;

import com.theitfox.camera.presentation.common.mvp.BaseView;

import java.io.File;

/**
 * Created by btquanto on 13/09/2016.
 */
public interface CameraView extends BaseView {
    void onSaveJPEGToSdCardSuccess(File file);

    void onSaveJPEGToSdCardError();

    void onGetLastPhotoTakenSuccess(Bitmap bitmap);

    void onGetLastPhotoTakenError();
}
