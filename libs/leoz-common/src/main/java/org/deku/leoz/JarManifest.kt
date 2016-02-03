package org.deku.leoz

import java.io.File
import java.util.jar.JarFile
import java.util.jar.Manifest

/**
 * Created by masc on 09.10.15.
 */
class JarManifest(type: Class<out Any>) {
    private val manifest: Manifest

    init {
        val loc = type.protectionDomain.codeSource.location
        val jar = JarFile(File(loc.toURI()))
        this.manifest = jar.manifest
    }

    /**
     * Implementation name or empty string if it doesn't exist
     */
    val implementationName: String by lazy({
        try {
            manifest.mainAttributes.getValue("Implementation-Name")
        } catch(e: IllegalArgumentException) {
            ""
        }
    })

    /**
     * Implementation version or empty string if it doesn't exist
     */
    val implementationVersion: String by lazy({
        try {
            manifest.mainAttributes.getValue("Implementation-Version")
        } catch(e: IllegalArgumentException) {
            ""
        }
    })
}