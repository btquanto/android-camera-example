package com.theitfox.camera.domain.usecases;

import android.graphics.Bitmap;

import com.theitfox.camera.data.repositories.BitmapRepository;
import com.theitfox.camera.domain.usecases.abstracts.UseCase;

import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by btquanto on 02/12/2016.
 */

public class GetLastPhotoTaken extends UseCase<Bitmap> {

    private static final int PHOTO_IN_SAMPLING_SIZE = 8;

    private BitmapRepository repository;

    public GetLastPhotoTaken(@Named("executionThread") Scheduler executionThread,
                             @Named("postExecutionThread") Scheduler postExecutionThread,
                             BitmapRepository repository) {
        super(executionThread, postExecutionThread);
        this.repository = repository;
    }

    @Override
    protected Observable<Bitmap> buildUseCaseObservable() {
        return repository.getLastPhotoTaken(PHOTO_IN_SAMPLING_SIZE);
    }
}
