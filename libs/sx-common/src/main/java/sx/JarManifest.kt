package sx

import java.io.File
import java.util.jar.JarFile
import java.util.jar.Manifest

/**
 * Created by masc on 09.10.15.
 */
class JarManifest(type: Class<out Any>) {
    init {
    }

    private val manifest: Manifest by lazy({
        val loc = type.protectionDomain.codeSource.location
        val jar = JarFile(File(loc.toURI()))
        jar.manifest
    })

    /**
     * Implementation name or empty string if it doesn't exist
     */
    val implementationName: String by lazy({
        try {
            manifest.mainAttributes.getValue("Implementation-Name") ?: ""
        } catch(e: Exception) {
            ""
        }
    })

    /**
     * Implementation version or empty string if it doesn't exist
     */
    val implementationVersion: String by lazy({
        try {
            manifest.mainAttributes.getValue("Implementation-Version") ?: ""
        } catch(e: Exception) {
            ""
        }
    })
}