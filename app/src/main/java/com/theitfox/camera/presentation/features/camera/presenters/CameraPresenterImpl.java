package com.theitfox.camera.presentation.features.camera.presenters;

import android.graphics.Bitmap;
import android.hardware.Camera;

import com.theitfox.camera.presentation.features.camera.presenters.abstracts.CameraPresenter;
import com.theitfox.camera.presentation.features.camera.presenters.abstracts.CameraPresenterUseCaseProvider;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by btquanto on 29/11/2016.
 */
public class CameraPresenterImpl extends CameraPresenter {

    private Scheduler executionThread;
    private Scheduler postExecutionThread;

    @Inject
    public CameraPresenterImpl(
            @Named("executionThread") Scheduler executionThread,
            @Named("postExecutionThread") Scheduler postExecutionThread,
            CameraPresenterUseCaseProvider useCaseProvider) {
        super(useCaseProvider);
        this.executionThread = executionThread;
        this.postExecutionThread = postExecutionThread;
    }

    @Override
    public void saveJPEGToSdCard(byte[] jpeg, String fileName) {
        useCaseProvider.getSaveJPEGToSdCardUseCase(jpeg, fileName)
                .onError(this::onSaveJPEGToSdCardError)
                .onNext(this::onSaveJPEFToSdCardSuccess)
                .execute();
    }

    private void onSaveJPEGToSdCardError(Throwable e) {
        if (isViewAttached()) {
            view.onSaveJPEGToSdCardError();
        }
    }

    private void onSaveJPEFToSdCardSuccess(File file) {
        if (isViewAttached()) {
            view.onSaveJPEGToSdCardSuccess(file);
        }
    }

    @Override public void getLastPhotoTaken() {
        useCaseProvider.getGetLastPhotoTakenUseCase()
                .onError(this::onGetLastPhotoTakenError)
                .onNext(this::onGetLastPhotoTakenSuccess)
                .execute();
    }

    private void onGetLastPhotoTakenError(Throwable e) {
        if (isViewAttached()) {
            view.onGetLastPhotoTakenError();
        }
    }


    private void onGetLastPhotoTakenSuccess(Bitmap bitmap) {
        if (isViewAttached()) {
            view.onGetLastPhotoTakenSuccess(bitmap);
        }
    }

    @Override
    public void openCamera(int cameraId) {
        Observable.<Camera>create(subscriber -> {
            // Open Camera on another thread for faster fragment starting time
            int numCams = Camera.getNumberOfCameras();
            if (numCams > 0) {
                Camera camera = Camera.open(cameraId);
                subscriber.onNext(camera);
            }
        }).subscribeOn(executionThread)
                .observeOn(postExecutionThread)
                .subscribe(this::onOpenCameraSuccess, this::onOpenCameraError);
    }

    void onOpenCameraSuccess(Camera camera) {
        if (isViewAttached()) {
            view.onOpenCameraSuccess(camera);
        }
    }

    void onOpenCameraError(Throwable e) {
        if (isViewAttached()) {
            view.onOpenCameraError();
        }
    }

    @Override
    public void closeCamera(Camera camera) {

    }
}
