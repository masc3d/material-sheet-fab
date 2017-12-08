package sx.android.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.*;

import java.io.IOException;
import java.util.List;

import sx.android.Function;

/**
 * Camera view
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {
    private SurfaceHolder mHolder;
    private Function<Void, Camera> mCameraProvider;
    private Camera mCamera;

    boolean mHasSurface = false;

    private static final String TAG = "CAMERAVIEW";

    public CameraView(Context context, Function<Void, Camera> cameraProvider) {
        super(context);
        mCameraProvider = cameraProvider;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = this.getHolder();
        mHolder.addCallback(this);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
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

    /**
     * Has to be called when parent view is resumed, as android may close camera
     */
    public void update() {
        Log.d(CameraView.class.getName(), "CameraView updating");

        // Get new instance from camera provider
        // TODO async call, this may take longer when the hardware camera was released meanwhile (when application caches hardware camera instance)
        mCamera = mCameraProvider.apply(null);

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(this.getHolder());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 90;
                break;
            case Surface.ROTATION_90:
                degrees = 0;
                break;
            case Surface.ROTATION_180:
                degrees = 270;
                break;
            case Surface.ROTATION_270:
                degrees = 180;
                break;
        }
        mCamera.setDisplayOrientation(degrees);

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = this.getOptimalPreviewSize(previewSizes, this.getWidth(), this.getHeight());

        parameters.setPreviewSize(previewSize.width, previewSize.height);
        Log.v(TAG, String.format("UPDATE ROTATION %d", rotation));
        parameters.setRotation(rotation);

        mCamera.setParameters(parameters);
        mCamera.startPreview();

        mHasSurface = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(CameraView.class.getName(), "CameraView surfaceCreated");

        this.update();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (mCamera != null)
                // Camera may have already been stopped and released by application/activity logic when app goes to background
                mCamera.stopPreview();
        } catch (Exception e) {
            // Ignore stopPreview failing
        }
        mCamera = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(CameraView.class.getName(), "CameraView surfaceChanged");
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(CameraView.class.getName(), "CameraView::onAttachedToWindow");
        super.onAttachedToWindow();
    }

    public boolean hasSurface() {
        return mHasSurface;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }
}