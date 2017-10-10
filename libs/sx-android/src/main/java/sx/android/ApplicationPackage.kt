package sx.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Android application package
 * Created by masc on 19/02/2017.
 */
class ApplicationPackage(
        val file: File) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Install APK file
     *
     * This method is compliant with the new FileProviders introduced in API level 25.
     * https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     * Thus requires `provider` definition in manifest and referring provider paths resource.
     */
    @SuppressLint("SetWorldReadable")
    fun install(context: Context) {
        log.info("Installing apk [${this.file}]")

        // Make sure file is accessible for the system
        this.file.setReadable(true, false)

        val i = Intent()
        i.action = Intent.ACTION_VIEW

        val apkUri: Uri
        var intentFlags: Int = Intent.FLAG_ACTIVITY_NEW_TASK

        if (Build.VERSION.SDK_INT >= 24) {
            // Use file provider based method
            apkUri = FileProvider.getUriForFile(
                    context,
                    // Authority
                    "${context.getApplicationContext().getPackageName()}.provider",
                    // File URI
                    file)

            intentFlags = intentFlags or Intent.FLAG_GRANT_READ_URI_PERMISSION
        } else {
            // Older versions cannot handle content URIs apparently,
            // thus falling back to passing regular file URI to intent
            apkUri = Uri.fromFile(this.file)
        }

        i.setDataAndType(apkUri, "application/vnd.android.package-archive")
        i.addFlags(intentFlags)
        context.startActivity(i)
    }
}