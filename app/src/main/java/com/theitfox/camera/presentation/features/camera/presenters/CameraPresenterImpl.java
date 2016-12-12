package com.theitfox.camera.presentation.features.camera.presenters;

import android.graphics.Bitmap;

import com.theitfox.camera.presentation.features.camera.presenters.abstracts.CameraPresenter;
import com.theitfox.camera.presentation.features.camera.presenters.abstracts.CameraPresenterUseCaseProvider;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by btquanto on 29/11/2016.
 */

public class CameraPresenterImpl extends CameraPresenter {

    @Inject
    public CameraPresenterImpl(CameraPresenterUseCaseProvider useCaseProvider) {
        super(useCaseProvider);
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
}
