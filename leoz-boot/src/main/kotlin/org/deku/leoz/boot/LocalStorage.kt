package org.deku.leoz.boot

import org.deku.leoz.bundle.Bundles
import java.io.File
import java.nio.file.Paths

/**
 * Created by masc on 21.09.15.
 */
object LocalStorage : org.deku.leoz.LocalStorage(Bundles.LEOZ_BOOT) {
    /** Base path of native bundle.
     * @return Path of bundle or null if path could not be detected
     */
    val nativeBundleBasePath: File? by lazy(fun (): File? {
        val codeSourcePath = Paths.get(this.javaClass.protectionDomain.codeSource.location.toURI())

        if (!codeSourcePath.toString().endsWith(".jar"))
            return null

        return codeSourcePath.parent.parent.toFile()
    })
}