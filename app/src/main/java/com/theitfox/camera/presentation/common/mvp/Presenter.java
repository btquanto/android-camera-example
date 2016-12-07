package com.theitfox.camera.presentation.common.mvp;


import rx.subscriptions.CompositeSubscription;

/**
 * Created by btquanto on 15/08/2016.
 * <p>
 * The base Presenter that all Presenters extends from
 *
 * @param <V>   The type of mvp view that should be attached to this presenter
 * @param <UCP> the type parameter
 */
public abstract class Presenter<V extends BaseView, UCP extends UseCaseProvider> {
    /**
     * The mvp view that should be attached to this presenter.
     */
    protected V view;

    /**
     * The composite subscription that manages the life cycles of all use cases
     */
    protected CompositeSubscription subscription;

    /**
     * The Use case provider.
     */
    protected UCP useCaseProvider;

    /**
     * Instantiates a new Presenter.
     *
     * @param useCaseProvider the use case provider
     */
    public Presenter(UCP useCaseProvider) {
        this.useCaseProvider = useCaseProvider;
    }

    /**
     * Attach a view to this presenter
     *
     * @param view the view that will be attached to this presenter
     */
    public void attachView(V view) {
        this.view = view;
        this.subscription = new CompositeSubscription();
    }

    /**
     * Detach the view from this presenter
     */
    public void detachView() {
        this.view = null;
        this.subscription.unsubscribe();
    }

    /**
     * Check if there is a view is attached to this presenter
     *
     * @return the boolean
     */
    protected boolean isViewAttached() {
        return this.view != null;
    }


}
