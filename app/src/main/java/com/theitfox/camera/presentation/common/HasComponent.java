package com.theitfox.camera.presentation.common;

/**
 * Created by btquanto on 11/10/2016.
 */
public interface HasComponent {
    /**
     * Gets component.
     *
     * @param <C> the type parameter
     * @return the component
     */
    <C> C getComponent();
}
