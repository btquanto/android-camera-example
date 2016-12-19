package com.theitfox.camera.presentation.features.camera.presenters.abstracts;

import android.hardware.Camera;

import com.theitfox.camera.presentation.common.mvp.Presenter;
import com.theitfox.camera.presentation.features.camera.views.abstracts.CameraView;

/**
 * Created by btquanto on 13/09/2016.
 */
public abstract class CameraPresenter extends Presenter<CameraView, CameraPresenterUseCaseProvider> {
    /**
     * Instantiates a new Camera presenter.
     *
     * @param useCaseProvider the use case provider
     */
    public CameraPresenter(CameraPresenterUseCaseProvider useCaseProvider) {
        super(useCaseProvider);
    }

    public abstract void saveJPEGToSdCard(byte[] jpeg, String fileName);

    public abstract void getLastPhotoTaken();

    public abstract void openCamera(int cameraId);

    public abstract void closeCamera(Camera camera);
}
