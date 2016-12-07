package com.theitfox.camera.presentation.features.camera.presenters;

import com.theitfox.camera.presentation.features.camera.presenters.abstracts.CameraPresenter;
import com.theitfox.camera.presentation.features.camera.presenters.abstracts.CameraPresenterUseCaseProvider;

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
                .onError(e -> {
                    if (isViewAttached()) {
                        view.onSaveJPEGToSdCardError();
                    }
                })
                .onNext(file -> {
                    if (isViewAttached()) {
                        view.onSaveJPEGToSdCardSuccess(file);
                    }
                })
                .execute();
    }

    @Override public void getLastPhotoTaken() {
        useCaseProvider.getGetLastPhotoTakenUseCase()
                .onError(e -> {
                    if (isViewAttached()) {
                        view.onGetLastPhotoTakenError();
                    }
                })
                .onNext(bitmap -> {
                    if (isViewAttached()) {
                        view.onGetLastPhotoTakenSuccess(bitmap);
                    }
                })
                .execute();
    }
}
