package com.theitfox.camera.presentation.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

/**
 * Created by btquanto on 28/09/2016.
 *
 * @param <T> the type parameter
 */
public class DiffTool<T> extends DiffUtil.Callback implements Observable.OnSubscribe<DiffUtil.DiffResult> {
    /**
     * The On complete action.
     */
    protected Action0 onCompleteAction;
    /**
     * The On error action.
     */
    protected Action1<Throwable> onErrorAction;
    /**
     * The On next action.
     */
    protected Action1<DiffUtil.DiffResult> onNextAction;
    private List<T> oldList, newList;
    private ItemContentComparable itemContentComparable;
    private ItemComparable itemComparable;

    /**
     * Instantiates a new Diff tool.
     *
     * @param oldList the old list
     * @param newList the new list
     */
    public DiffTool(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
        this.onCompleteAction = Actions.empty();
        this.onErrorAction = Actions.empty();
        this.onNextAction = Actions.empty();
    }

    /**
     * On compare items diff tool.
     *
     * @param itemComparable the item comparable
     * @return the diff tool
     */
    public DiffTool<T> onCompareItems(ItemComparable<T> itemComparable) {
        this.itemComparable = itemComparable;
        return this;
    }

    /**
     * On compare item contents diff tool.
     *
     * @param itemContentComparable the item content comparable
     * @return the diff tool
     */
    public DiffTool<T> onCompareItemContents(ItemContentComparable<T> itemContentComparable) {
        this.itemContentComparable = itemContentComparable;
        return this;
    }

    /**
     * On complete diff tool.
     *
     * @param action the action
     * @return the diff tool
     */
    public DiffTool<T> onComplete(Action0 action) {
        this.onCompleteAction = action;
        return this;
    }

    /**
     * On error diff tool.
     *
     * @param action the action
     * @return the diff tool
     */
    public DiffTool<T> onError(Action1<Throwable> action) {
        this.onErrorAction = action;
        return this;
    }

    /**
     * On next diff tool.
     *
     * @param action the action
     * @return the diff tool
     */
    public DiffTool<T> onNext(Action1<DiffUtil.DiffResult> action) {
        this.onNextAction = action;
        return this;
    }

    @Override
    public void call(Subscriber<? super DiffUtil.DiffResult> subscriber) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(this);
        subscriber.onNext(diffResult);
    }

    /**
     * Execute subscription.
     *
     * @return the subscription
     */
    public Subscription execute() {
        return Observable.create(this)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, onErrorAction, onCompleteAction);
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);

        if (itemComparable != null) {
            return itemComparable.areItemsTheSame(oldItem, newItem);
        }
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);

        if (itemContentComparable != null) {
            return itemContentComparable.areItemsEqual(oldItem, newItem);
        }
        return oldItem.equals(newItem);
    }

    /**
     * The interface Item content comparable.
     *
     * @param <T> the type parameter
     */
    public interface ItemContentComparable<T> {
        /**
         * Are items equal boolean.
         *
         * @param oldItem the old item
         * @param newItem the new item
         * @return the boolean
         */
        boolean areItemsEqual(T oldItem, T newItem);
    }

    /**
     * The interface Item comparable.
     *
     * @param <T> the type parameter
     */
    public interface ItemComparable<T> {
        /**
         * Are items the same boolean.
         *
         * @param oldItem the old item
         * @param newItem the new item
         * @return the boolean
         */
        boolean areItemsTheSame(T oldItem, T newItem);
    }

}
