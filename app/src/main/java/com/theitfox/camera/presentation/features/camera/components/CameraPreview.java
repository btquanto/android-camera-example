package com.theitfox.camera.presentation.features.camera.components;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by btquanto on 23/11/2016.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private List<Camera.Size> supportedPreviewSizes;
    private Camera.Size previewSize;

    private boolean isSurfaceCreated;
    private int surfaceRotation;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.surfaceHolder = getHolder();
        this.isSurfaceCreated = false;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        this.surfaceHolder.addCallback(this);
    }

    public void setCamera(Camera camera, int cameraId, int displayOrientation) {
        this.camera = camera;

        if (camera != null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int cameraRotation = calculateCameraRotation(info, displayOrientation);
            this.surfaceRotation = calculateSurfaceRotation(info, cameraRotation);

            Camera.Parameters params = camera.getParameters();
            params.setRotation(cameraRotation);
            supportedPreviewSizes = params.getSupportedPreviewSizes();
            requestLayout();

            try {
                if (isSurfaceCreated) {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.setDisplayOrientation(surfaceRotation);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            camera.setParameters(params);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if (camera != null) {
                camera.setPreviewDisplay(surfaceHolder);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        isSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null || camera == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception ignored) {
            // Ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        try {
            Camera.Parameters params = camera.getParameters();
            params.setPreviewSize(previewSize.width, previewSize.height);
            camera.setDisplayOrientation(surfaceRotation);
            camera.setParameters(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception ignored) {
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceCreated = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, getWidth(), getHeight());
            if (camera != null) {
                try {
                    Camera.Parameters params = camera.getParameters();
                    params.setPreviewSize(previewSize.width, previewSize.height);
                    camera.setDisplayOrientation(surfaceRotation);
                    camera.setParameters(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onLayout(changed, l, t, r, b);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private int calculateSurfaceRotation(Camera.CameraInfo info, int cameraRotation) {
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            return cameraRotation;
        }
        return (360 - cameraRotation) % 360;
    }

    private int calculateCameraRotation(Camera.CameraInfo info, int displayOrientation) {
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            return (450 - displayOrientation) % 360;
        }
        return (270 + displayOrientation) % 360;
    }
}
