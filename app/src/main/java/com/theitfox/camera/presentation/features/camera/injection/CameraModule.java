package com.theitfox.camera.presentation.features.camera.injection;

import android.hardware.Camera;

import com.theitfox.camera.data.repositories.BitmapRepository;
import com.theitfox.camera.domain.usecases.GetLastPhotoTaken;
import com.theitfox.camera.domain.usecases.SaveJPEGToSdCardUseCase;
import com.theitfox.camera.presentation.common.injection.ApplicationModule;
import com.theitfox.camera.presentation.features.camera.components.CameraTouchController;
import com.theitfox.camera.presentation.features.camera.presenters.abstracts.CameraPresenterUseCaseProvider;
import com.theitfox.camera.presentation.features.camera.views.CameraFragment;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

/**
 * Created by btquanto on 23/11/2016.
 */
@Module(includes = ApplicationModule.class)
public class CameraModule {
    private CameraFragment fragment;

    public CameraModule(CameraFragment fragment) {
        this.fragment = fragment;
    }

    @Provides @Singleton Camera camera() {
        Camera camera = null;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return camera;
    }

    @Provides @Singleton
    CameraPresenterUseCaseProvider cameraPresenterUseCaseProvider(@Named("ioThread") Scheduler ioThread,
                                                                  @Named("executionThread") Scheduler executionThread,
                                                                  @Named("postExecutionThread") Scheduler postExecutionThread,
                                                                  BitmapRepository repository) {
        return new CameraPresenterUseCaseProvider() {
            @Override
            public SaveJPEGToSdCardUseCase getSaveJPEGToSdCardUseCase(byte[] jpeg, String fileName) {
                return new SaveJPEGToSdCardUseCase(ioThread, postExecutionThread, repository, jpeg, fileName);
            }

            @Override public GetLastPhotoTaken getGetLastPhotoTakenUseCase() {
                return new GetLastPhotoTaken(executionThread, postExecutionThread, repository);
            }
        };
    }

    @Provides @Singleton CameraTouchController.CameraTouchListener zoomControllerListener() {
        return fragment;
    }
}
