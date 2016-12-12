package com.theitfox.camera.presentation.features.camera.components;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by btquanto on 12/12/2016.
 */

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private List<Camera.Size> supportedPreviewSizes;
    private Camera.Size previewSize;

    private boolean isSurfaceCreated;
    private int surfaceRotation;

    public CameraPreview(Context context) {
        super(context);
        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        surfaceView = new SurfaceView(context);
        surfaceHolder = surfaceView.getHolder();
        isSurfaceCreated = false;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);
        surfaceView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(surfaceView);
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);
            if (supportedPreviewSizes != null) {
                previewSize = getOptimalPreviewSize(supportedPreviewSizes, getWidth(), getHeight());
                if (camera != null) {
                    try {
                        float ratio = ((float) previewSize.width) / previewSize.height;

                        int width = getWidth();
                        int height = (int) (width * ratio);
                        if (height > getHeight()) {
                            height = getHeight();
                            width = (int) (height / ratio);
                        }

                        l = (getWidth() - width) / 2;
                        r = l + width;
                        t = (getHeight() - height) / 2;
                        b = t + height;

                        Camera.Parameters params = camera.getParameters();
                        params.setPreviewSize(previewSize.width, previewSize.height);
                        camera.setDisplayOrientation(surfaceRotation);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            child.layout(l, t, r, b);
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
