package sx.android;

/**
 * Camera accessor with caching support
 * Created by masc on 26.11.14.
 */
public class Camera {
    private static Camera mInstance;

    /**
     * Singleton instance accessor
     *
     * @return
     */
    public static Camera getInstance() {
        if (mInstance == null) {
            synchronized (Camera.class) {
                mInstance = new Camera();
            }
        }
        return mInstance;
    }

    android.hardware.Camera mHardwareCamera;

    /**
     * Hardware camera instance
     *
     * @return
     */
    public android.hardware.Camera getHardwareCamera() {
        // Preserve hardware camera instance for faster access.
        if (mHardwareCamera == null) {
            mHardwareCamera = android.hardware.Camera.open();
        }
        return mHardwareCamera;
    }

    /**
     * Release hardware camera
     */
    public void releaseHardwareCamera() {
        if (mHardwareCamera != null) {
            try {
                mHardwareCamera.stopPreview();
            } catch (Exception e) {
                // Ignore stopPreview failing
            }
            mHardwareCamera.release();
            mHardwareCamera = null;
        }
    }
}
