package org.deku.leoz.build

import org.apache.commons.lang3.SystemUtils
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.File
import java.nio.file.Paths

/**
 * Native packager bundle
 * Created by masc on 10.09.15.
 */
public class Bundle(
        public val name: String,
        public val os: OperatingSystem,
        public val basePath: File) {
    /** Bundle content path */
    public val contentPath: File
    /** Jar path */
    public val jarPath: File

    init {
        if (os == OperatingSystem.OSX) {
            val nioBasePath = basePath.toPath()
            val nioContentPath = nioBasePath
                    .resolve(this.name + ".app")
                    .resolve("Contents")

            jarPath = nioContentPath
                    .resolve("Java")
                    .toFile()

            contentPath = nioContentPath.toFile()
        } else {
            contentPath = basePath
            jarPath = File(contentPath, "app")
        }
    }

    companion object {
    }

    /**
     * Native packager bundle configuration (file)
     * Created by masc on 10.09.15.
     */
    public inner class Configuration() {
        /** c'tor. loads configuration from file */
        init {

        }
    }
}
