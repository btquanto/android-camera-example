package com.theitfox.camera.presentation.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.theitfox.camera.R;

import butterknife.ButterKnife;

/**
 * Created by btquanto on 30/09/2016.
 * <p>
 * Super class of Activities that only have a single fragment
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_single_fragment);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        ButterKnife.bind(this);


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = onCreateFragment();
        fm.beginTransaction()
                .replace(R.id.fl_fragment_container, fragment)
                .commit();
    }

    /**
     * Is full screen boolean.
     *
     * @return the boolean
     */
    protected boolean isFullScreen() {
        return false;
    }

    /**
     * Override this to specify which fragment should this activity contains
     *
     * @return the fragment that this activity should contains
     */
    protected abstract Fragment onCreateFragment();
}
