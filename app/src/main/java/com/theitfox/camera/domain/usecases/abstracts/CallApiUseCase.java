package com.theitfox.camera.domain.usecases.abstracts;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by btquanto on 29/08/2016.
 * <p>
 * This class is the super class of use cases that handle API calls
 * This allows use cases that handle API calls to first try to get the results from cache
 * then from network
 *
 * @param <T> the type parameter
 */
public abstract class CallApiUseCase<T> extends UseCase<T> {

    /**
     * Instantiates a new Call API use case.
     *
     * @param executionThread     the execution thread
     * @param postExecutionThread the post execution thread
     */
    public CallApiUseCase(Scheduler executionThread, Scheduler postExecutionThread) {
        super(executionThread, postExecutionThread);
    }

    @Override
    protected Observable<T> buildExecutionObservable() {
        Observable<T> queryCacheObservable = this.buildCacheRetrievalObservable();
        Observable<T> useCaseObservable = super.buildExecutionObservable();
        if (queryCacheObservable != null) {
            return queryCacheObservable
                    .mergeWith(useCaseObservable)
                    .filter(response -> response != null);
        }
        return useCaseObservable;
    }

    /**
     * Build an observable that promises the cached API response
     *
     * @return the observable
     */
    protected Observable<T> buildCacheRetrievalObservable() {
        return null;
    }
}
