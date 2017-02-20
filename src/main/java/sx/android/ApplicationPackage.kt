package sx.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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
     */
    @SuppressLint("SetWorldReadable")
    fun install(context: Context) {
        log.info("Installing apk [${this.file}]")

        // Make sure file is accessible for the system
        this.file.setReadable(true, false)

        val i = Intent()
        i.action = Intent.ACTION_VIEW
        i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }
}