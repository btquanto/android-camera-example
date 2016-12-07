package com.theitfox.camera.presentation.features.camera.presenters.abstracts;

import com.theitfox.camera.domain.usecases.GetLastPhotoTaken;
import com.theitfox.camera.domain.usecases.SaveJPEGToSdCardUseCase;
import com.theitfox.camera.presentation.common.mvp.UseCaseProvider;

/**
 * The interface Camera presenter use case provider.
 */
public interface CameraPresenterUseCaseProvider extends UseCaseProvider {
    SaveJPEGToSdCardUseCase getSaveJPEGToSdCardUseCase(byte[] jpeg, String fileName);

    GetLastPhotoTaken getGetLastPhotoTakenUseCase();
}
