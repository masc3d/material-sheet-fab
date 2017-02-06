package sx.android

/**
 * Camera accessor with caching support
 * Created by masc on 26.11.14.
 */
@Suppress("DEPRECATION")
class Camera {
    private var _hardwareCamera: android.hardware.Camera? = null
    /**
     * Hardware camera instance
     * @return
     */
    // Preserve hardware camera instance for faster access.
    val hardwareCamera: android.hardware.Camera
        get() {
            if (_hardwareCamera == null) {
                _hardwareCamera = android.hardware.Camera.open();

            }
            return _hardwareCamera!!
        }

    /**
     * Release hardware camera
     */
    fun releaseHardwareCamera() {
        val camera = _hardwareCamera
        if (camera != null) {
            try {
                camera.stopPreview()
            } catch(e: Throwable) {
                // Ignore stopPreview failing
            }
            camera.release()
            _hardwareCamera = null
        }
    }

    companion object {
        @JvmStatic val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Camera()
        }
    }
}
