package com.theitfox.camera.presentation.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.theitfox.camera.presentation.common.listeners.FragmentActionListener;

/**
 * Created by btquanto on 05/10/2016.
 */
public abstract class BaseFragment extends Fragment implements HasComponent {

    protected FragmentActionListener fragmentActionListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void closeFragment() {
        if (fragmentActionListener != null) {
            fragmentActionListener.closeFragment();
        } else {
            getActivity().onBackPressed();
        }
    }
}
