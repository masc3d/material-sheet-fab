package org.deku.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Plugin extension class, used for extension within build.gradle
 */
class PackagerPluginExtension {
    def String title

    /** Packager base directory */
    def File packagerBaseDir

    /** Release directory base path */
    def File releaseBasePath
    /** Path to .icns file */
    def File osxIcon
    /** Path to .ico file */
    def File windowsIcon

    /** Map of source -> (relative) destination directories to copy to bundle release directory */
    def supplementalDirs(LinkedHashMap<File, File> dirs) {
        mSupplementalDirs = dirs
    }

    /** Map of source dirs containing arch subdirs -> (relative) destination directories to copy to bundle release directory */
    def supplementalArchDirs(LinkedHashMap<File, File> dirs) {
        mSupplementalArchDirs = dirs
    }

    private def LinkedHashMap<File, File> mSupplementalDirs
    public LinkedHashMap<File, File> getSupplementalDirs() {
        return mSupplementalDirs
    }

    private def LinkedHashMap<File, File> mSupplementalArchDirs
    public LinkedHashMap<File, File> getSupplementalArchDirs() {
        return mSupplementalArchDirs
    }
}

/**
 * Packager plugin
 */
class PackagerPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Add extension extensino
        def ext = new PackagerPluginExtension()
        ext.packagerBaseDir = new File(project.buildDir, 'packager')
        ext.releaseBasePath = Paths.get(ext.packagerBaseDir.toURI())
                .resolve('release')
                .toFile()

        project.extensions.packager = ext

        // Bundle task
        project.tasks.create('packagerBundle', PackagerBundleTask) {
            extension = ext
            jvmOptions = "-XX:+UseCompressedOops"
        }
        project.tasks.packagerBundle.dependsOn(project.tasks.jar)

        // Release bundle task
        project.tasks.create('packagerReleaseBundle', PackagerReleaseBundleTask) {
            extension = ext
        }
        project.tasks.packagerReleaseBundle.dependsOn(project.tasks.packagerBundle)

        // Release jars task
        project.tasks.create('packagerReleaseJars', PackagerReleaseJarsTask) {
            extension = ext
        }
        project.tasks.packagerReleaseJars.dependsOn(project.tasks.jar)

        // Release push task
        project.tasks.create('packagerReleasePush', PackagerReleasePushTask) {
            extension = ext
        }
        project.tasks.packagerReleasePush.dependsOn(project.tasks.packagerReleaseJars)
    }
}

