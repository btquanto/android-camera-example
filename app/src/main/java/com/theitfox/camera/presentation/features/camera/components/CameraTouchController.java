package com.theitfox.camera.presentation.features.camera.components;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.theitfox.camera.R;

import javax.inject.Inject;

import butterknife.OnTouch;

/**
 * Created by btquanto on 29/11/2016.
 */

public class CameraTouchController {

    private static final int FOCUS_AREA_SIZE = 300;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int ROTATE = 3;
    private static final int SCALE_FACTOR = 5;

    // CameraTouchController Listener
    private CameraTouchListener listener;
    // Current mode
    private int mode = NONE;

    // Zooming positions
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float[] lastEvent = null;

    //TODO: The naming does not make sense
    private int zoomLevel;
    private int scaleLevel;

    // The camera preview
    private CameraPreview cameraPreview;

    @Inject
    public CameraTouchController(CameraTouchListener listener) {
        this.mode = NONE;
        this.listener = listener;
        this.zoomLevel = 0;
        this.scaleLevel = 1;
    }

    public void setCameraPreview(CameraPreview cameraPreview) {
        this.cameraPreview = cameraPreview;
    }

    @OnTouch(R.id.sv_camera_preview)
    public boolean onTouch(View view, MotionEvent event) {
        final int actionPerformed = event.getAction() & MotionEvent.ACTION_MASK;

        switch (actionPerformed) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == DRAG) {
                    listener.onFocus(calculateFocusArea(event.getX(), event.getY()));
                }
                mode = NONE;
                lastEvent = null;
                // TODO: mode may switch back to DRAG
                zoomLevel = scaleLevel;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        float scale = (newDist / oldDist);
                        int prevScaleLevel = scaleLevel;
                        if (scale > 1) {
                            scaleLevel = (int) (zoomLevel + SCALE_FACTOR * scale);
                        } else {
                            scaleLevel = (int) (zoomLevel * scale);
                        }
                        if (scaleLevel != prevScaleLevel) {
                            scaleLevel = listener.onZoom(scaleLevel);
                        }
                    }
                }

                break;
        }
        return true;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / cameraPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / cameraPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }

    public interface CameraTouchListener {
        int onZoom(int zoomLevel);

        void onFocus(Rect focusRect);
    }
}
