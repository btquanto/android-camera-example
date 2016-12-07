package com.theitfox.camera.domain.usecases;

import com.theitfox.camera.data.repositories.BitmapRepository;
import com.theitfox.camera.domain.usecases.abstracts.UseCase;

import java.io.File;

import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by btquanto on 29/11/2016.
 */

public class SaveJPEGToSdCardUseCase extends UseCase<File> {

    private BitmapRepository repository;
    private byte[] jpeg;
    private String fileName;

    public SaveJPEGToSdCardUseCase(@Named("ioThread") Scheduler executionThread,
                                   @Named("postExecutionThread") Scheduler postExecutionThread,
                                   BitmapRepository repository,
                                   byte[] jpeg,
                                   String fileName) {
        super(executionThread, postExecutionThread);
        this.repository = repository;
        this.jpeg = jpeg;
        this.fileName = fileName;
    }

    @Override
    protected Observable<File> buildUseCaseObservable() {
        return repository.saveJPEGToSdCard(jpeg, String.format("%s.jpg", fileName));
    }
}
