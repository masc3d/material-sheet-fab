package sx.android

/**
 * Camera accessor with caching support
 * Created by masc on 26.11.14.
 */
class Camera {
    /**
     * Hardware camera instance
     * @return
     */
    // Preserve hardware camera instance for faster access.
    val hardwareCamera: android.hardware.Camera by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        android.hardware.Camera.open()
    }

    /**
     * Release hardware camera
     */
    fun releaseHardwareCamera() {
        try {
            this.hardwareCamera.stopPreview()
        } catch(e: Throwable) {
            // Ignore stopPreview failing
        }
        this.hardwareCamera.release()
    }

    companion object {
        @JvmStatic val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Camera()
        }
    }
}
