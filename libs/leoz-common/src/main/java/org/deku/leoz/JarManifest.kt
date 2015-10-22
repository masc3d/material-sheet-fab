package org.deku.leoz

import java.io.File
import java.util.jar.JarFile
import java.util.jar.Manifest

/**
 * Created by masc on 09.10.15.
 */
class JarManifest(type: Class<Any>) {
    private val manifest: Manifest

    init {
        val loc = type.protectionDomain.codeSource.location
        val jar = JarFile(File(loc.toURI()))
        this.manifest = jar.manifest
    }

    val implementationName: String by lazy({
        manifest.mainAttributes.getValue("Implementation-Name")
    })

    val implementationVersion: String by lazy({
        manifest.mainAttributes.getValue("Implementation-Version")
    })
}