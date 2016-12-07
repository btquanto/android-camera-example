package com.theitfox.camera.domain.usecases.abstracts;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by btquanto on 22/08/2016.
 * <p>
 * This class is the super class of use cases
 * Use cases represent business logic. Each use case represents one logic.
 *
 * @param <T> the type of event the use case promises to emit upon execution
 */
public abstract class UseCase<T> {

    /**
     * The business logic will run on this thread
     */
    protected final Scheduler executionThread;

    /**
     * The events will be emitted to this thread
     */
    protected final Scheduler postExecutionThread;

    /**
     * Execute when the use case is completed
     */
    protected Action0 onCompleteAction;

    /**
     * Execute when an error occurs
     */
    protected Action1<Throwable> onErrorAction;

    /**
     * Execute when an event is emitted
     */
    protected Action1<T> onNextAction;

    /**
     * Instantiates a new Use case.
     *
     * @param executionThread     the execution thread
     * @param postExecutionThread the post execution thread
     */
    public UseCase(Scheduler executionThread, Scheduler postExecutionThread) {
        this.executionThread = executionThread;
        this.postExecutionThread = postExecutionThread;
        this.onNextAction = Actions.empty();
        this.onErrorAction = Actions.empty();
        this.onCompleteAction = Actions.empty();
    }

    /**
     * Setter for onCompletionAction
     *
     * @param action An OnCompleteAction object
     * @return this use case
     */
    public UseCase<T> onComplete(Action0 action) {
        this.onCompleteAction = (action != null) ? action : Actions.empty();
        return this;
    }

    /**
     * Setter for onErrorAction
     *
     * @param action An OnErrorAction object
     * @return this use case
     */
    public UseCase<T> onError(Action1<Throwable> action) {
        this.onErrorAction = (action != null) ? action : Actions.empty();
        return this;
    }

    /**
     * Setter for onNextAction
     *
     * @param action An OnNextAction object
     * @return this use case
     */
    public UseCase<T> onNext(Action1<T> action) {
        this.onNextAction = (action != null) ? action : Actions.empty();
        return this;
    }

    /**
     * Build the Observable that promises the output of the use case
     *
     * @return an Observable that promises the output of this use case
     */
    protected abstract Observable<T> buildUseCaseObservable();

    /**
     * Build the final Observable to be executed
     * This by default just calls{@link #buildUseCaseObservable}
     * However, it can be overridden for extended implementation
     * For example, merging {@link #buildUseCaseObservable()} with another Observable
     *
     * @return an Observable that promises the output of this use case
     */
    protected Observable<T> buildExecutionObservable() {
        return buildUseCaseObservable();
    }

    /**
     * Subscribe the execution observable to {@link #executionThread}
     * And modify the Observable to have its emission and notifications to perform
     * on {@link #postExecutionThread}
     *
     * @return an Observable that promises the output of this use case
     */
    private Observable<T> buildObservable() {
        return buildExecutionObservable()
                .subscribeOn(executionThread)
                .observeOn(postExecutionThread);
    }

    /**
     * Execute the use case.
     * The emitted output will be processed by the actions that were set:
     * - onNextAction
     * - onErrorAction
     * - onCompleteAction
     * Any actions that were not set will be ignored
     *
     * @return the subscription that manages the life cycle of the execution observable
     */
    public Subscription execute() {
        return buildObservable()
                .subscribe(onNextAction, onErrorAction, onCompleteAction);
    }

    /**
     * Execute the use case.
     *
     * @param useCaseSubscriber A subscriber that will process the emitted output
     * @return the subscription that manages the life cycle of the execution observable
     */
    public Subscription execute(Subscriber<T> useCaseSubscriber) {
        return buildObservable()
                .subscribe(useCaseSubscriber);
    }

    /**
     * Execute the use case.
     *
     * @param onNextAction the on next action
     * @return the subscription that manages the life cycle of the execution observable
     */
    public Subscription execute(Action1<T> onNextAction) {
        return buildObservable()
                .subscribe(onNextAction, onErrorAction, onCompleteAction);
    }

    /**
     * Execute the use case.
     *
     * @param onNextAction  handle
     * @param onErrorAction the on error action
     * @return the subscription that manages the life cycle of the execution observable
     */
    public Subscription execute(Action1<T> onNextAction, Action1<Throwable> onErrorAction) {
        return buildObservable()
                .subscribe(onNextAction, onErrorAction, onCompleteAction);
    }

    /**
     * Execute the use case.
     *
     * @param onNextAction     the on next action
     * @param onErrorAction    the on error action
     * @param onCompleteAction the on complete action
     * @return the subscription that manages the life cycle of the execution observable
     */
    public Subscription execute(Action1<T> onNextAction, Action1<Throwable> onErrorAction, Action0 onCompleteAction) {
        return buildObservable()
                .subscribe(onNextAction, onErrorAction, onCompleteAction);
    }
}


