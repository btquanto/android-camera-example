package com.theitfox.camera.presentation.features.camera.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theitfox.camera.R;
import com.theitfox.camera.presentation.common.BaseFragment;
import com.theitfox.camera.presentation.common.injection.ApplicationModule;
import com.theitfox.camera.presentation.features.camera.components.CameraPreview;
import com.theitfox.camera.presentation.features.camera.components.CameraTouchController;
import com.theitfox.camera.presentation.features.camera.injection.CameraComponent;
import com.theitfox.camera.presentation.features.camera.injection.CameraModule;
import com.theitfox.camera.presentation.features.camera.injection.DaggerCameraComponent;
import com.theitfox.camera.presentation.features.camera.presenters.CameraPresenterImpl;
import com.theitfox.camera.presentation.features.camera.views.abstracts.CameraView;
import com.theitfox.camera.utils.Duplex;
import com.theitfox.camera.utils.MapUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by btquanto on 23/11/2016.
 */
public class CameraFragment extends BaseFragment implements CameraView, CameraTouchController.CameraTouchListener {

    @BindView(R.id.fl_camera_preview) FrameLayout flCameraPreview;
    @BindView(R.id.cp_camera_preview) CameraPreview cameraPreview;
    @BindView(R.id.ib_gallery) ImageButton ibGallery;

    @Inject CameraPresenterImpl presenter;
    @Inject CameraTouchController cameraTouchController;
    @Inject @Named("executionThread") Scheduler executionThread;
    @Inject @Named("ioThread") Scheduler ioThread;
    @Inject @Named("postExecutionThread") Scheduler postExecutionThread;
    @Inject MapUtils mapUtils;

    private Camera camera;
    private int cameraId;

    private int flashMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inject dependencies using Dagger
        getComponent().inject(this);

        // Initialize other private variables that are not external dependencies
        // TODO: Should have checked for camera availability
        // TODO: We probably would want to save these settings to applicationState, or sharedPreferences
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        flashMode = 0;

        // Attach CameraView to CameraPresenter
        presenter.attachView(this);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        // Inject inflated view to this fragment using Butterknife
        ButterKnife.bind(this, view);
        // Inject flCameraView to cameraTouchController using Butterknife
        ButterKnife.bind(cameraTouchController, flCameraPreview);

        cameraTouchController.setSurfaceView(cameraPreview.getSurfaceView());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable.<Camera>create(subscriber -> {
            // Open Camera on another thread for faster fragment starting time
            int numCams = Camera.getNumberOfCameras();
            if (numCams > 0) {
                Camera camera = Camera.open(cameraId);
                camera.startPreview();
                subscriber.onNext(camera);
            }
        }).subscribeOn(executionThread)
                .observeOn(postExecutionThread)
                .subscribe(this::onOpenCameraSuccess, this::onOpenCameraError);
        presenter.getLastPhotoTaken();
    }

    void onOpenCameraSuccess(Camera camera) {
        this.camera = camera;
        this.cameraPreview.setCamera(camera, cameraId, getDisplayRotation());
    }

    void onOpenCameraError(Throwable e) {
        Toast.makeText(getContext(), getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            cameraPreview.setCamera(null, cameraId, 0);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Detach CameraView from CameraPresenter
        presenter.detachView();
    }

    @Override
    public CameraComponent getComponent() {
        return DaggerCameraComponent.builder()
                .cameraModule(new CameraModule(this))
                .applicationModule(new ApplicationModule(getContext()))
                .build();
    }

    @OnClick(R.id.ib_shutter)
    void onShutterButtonClicked() {
        camera.takePicture(this::onShutter, this::onRawImageTaken, this::onJPEGTaken);
    }

    private void onShutter() {
        // TODO: play shutter sound
    }

    private void onRawImageTaken(byte[] data, Camera camera) {

    }

    private void onJPEGTaken(byte[] data, Camera camera) {
        String fileName = String.format("%s-%d", "IrisStudio", System.currentTimeMillis());
        presenter.saveJPEGToSdCard(data, fileName);
        camera.startPreview();
    }

    @OnClick(R.id.ib_flash)
    void onFlashButtonClicked(View view) {
        flashMode = (flashMode + 1) % 3;
        Camera.Parameters parameters = camera.getParameters();
        ImageButton flashBtn = (ImageButton) view;
        Map<Integer, Duplex> map = mapUtils.create(new Integer[]{0, 1, 2},
                new Duplex[]{
                        new Duplex(Camera.Parameters.FLASH_MODE_OFF, R.drawable.ic_flash_off),
                        new Duplex(Camera.Parameters.FLASH_MODE_ON, R.drawable.ic_flash_on),
                        new Duplex(Camera.Parameters.FLASH_MODE_AUTO, R.drawable.ic_flash_auto)
                });
        Duplex mapValue = map.get(flashMode);
        if (mapValue != null) {
            parameters.setFlashMode(mapValue.getFirst());
            flashBtn.setImageResource(mapValue.getSecond());
        }
        camera.setParameters(parameters);
    }

    @OnClick(R.id.ib_switch_camera)
    void onSwitchCameraButtonClicked(View view) {
        ImageButton switchBtn = (ImageButton) view;
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            switchBtn.setImageResource(R.drawable.ic_camera_rear);
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            switchBtn.setImageResource(R.drawable.ic_camera_front);
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        camera.stopPreview();
        camera.release();
        try {
            camera = Camera.open(cameraId);
            cameraPreview.setCamera(camera, cameraId, getDisplayRotation());
            camera.startPreview();
            Camera.Parameters parameters = camera.getParameters();
            Map<Integer, String> map = mapUtils.create(new Integer[]{0, 1, 2},
                    new String[]{
                            Camera.Parameters.FLASH_MODE_OFF,
                            Camera.Parameters.FLASH_MODE_ON,
                            Camera.Parameters.FLASH_MODE_AUTO});
            String mapValue = map.get(flashMode);
            if (mapValue != null) {
                // This is unlikely to happen
                parameters.setFlashMode(mapValue);
            }
            camera.setParameters(parameters);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Toast.makeText(getContext(), getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.ib_close)
    void onCloseButtonClicked(View view) {
        closeFragment();
    }

    @Override
    public void onSaveJPEGToSdCardSuccess(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        getContext().sendBroadcast(mediaScanIntent);
        Observable.<Bitmap>create(subscriber -> {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = null;
            try {
                InputStream is = new FileInputStream(file);
                Bitmap srcBitmap = BitmapFactory.decodeStream(is, null, options);
                int orientation = 0;
                try {
                    ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                } catch (Exception ignored) {
                }

                Matrix matrix = new Matrix();
                Map<Integer, Integer> rotationMap = mapUtils.create(new Integer[]{
                        ExifInterface.ORIENTATION_ROTATE_90,
                        ExifInterface.ORIENTATION_ROTATE_180,
                        ExifInterface.ORIENTATION_ROTATE_270}, new Integer[]{90, 180, 270});
                Integer rotation = rotationMap.get(orientation);
                matrix.postRotate(rotation != null ? rotation : 0);
                bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            subscriber.onNext(bitmap);
        }).subscribeOn(ioThread)
                .observeOn(postExecutionThread)
                .subscribe(bitmap -> {
                    if (bitmap != null) {
                        ibGallery.setImageBitmap(bitmap);
                    }
                });
    }

    @Override
    public void onSaveJPEGToSdCardError() {
        Toast.makeText(getContext(), getString(R.string.error_unable_to_save_photo), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetLastPhotoTakenSuccess(Bitmap bitmap) {
        if (bitmap != null) {
            ibGallery.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onGetLastPhotoTakenError() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onZoom(int zoomLevel) {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.isZoomSupported()) {
            if (zoomLevel > parameters.getMaxZoom()) {
                zoomLevel = parameters.getMaxZoom();
            }
            parameters.setZoom(zoomLevel);
            camera.setParameters(parameters);
        }
        return zoomLevel;
    }

    @Override
    public void onFocus(Rect focusRect) {
        if (camera != null) {
            camera.cancelAutoFocus();
            Camera.Parameters parameters = camera.getParameters();

            if (parameters.getMaxNumFocusAreas() > 0) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                List<Camera.Area> mylist = new ArrayList<>();
                mylist.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(mylist);
                try {
                    camera.setParameters(parameters);
                } catch (Exception ignore) {
                    // Focus mode not supported
                }
            }
            camera.autoFocus((success, camera) -> {
                camera.cancelAutoFocus();
                try {
                    Camera.Parameters params = camera.getParameters();
                    if (params.getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(params);
                    }
                } catch (Exception ignored) {
                    // Focus mode not supported
                }
            });
        }
    }

    private int getDisplayRotation() {
        int displayRotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        switch (displayRotation) {
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return 0;
        }
    }
}
