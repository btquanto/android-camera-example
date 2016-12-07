package com.theitfox.camera.presentation.features.camera.injection;

import com.theitfox.camera.presentation.features.camera.views.CameraFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by btquanto on 23/11/2016.
 */
@Singleton
@Component(modules = CameraModule.class)
public interface CameraComponent {
    void inject(CameraFragment fragment);
}
