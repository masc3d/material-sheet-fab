package org.deku.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by n3 on 02-Aug-15.
 */
/**
 * Plugin extension class, used for extension within build.gradle
 */
class PackagerPluginExtension {
    def String title

    // Common locations
    def File packagerBaseDir
    def File packagerArchDir

    def File releaseBasePath
    /** Path to .icns file */
    def File osxIcon
    /** Path to .ico file */
    def File windowsIcon
}

/**
 * Packager plugin
 */
class PackagerPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Add extension extensino
        def ext = new PackagerPluginExtension()
        ext.packagerBaseDir = new File(project.buildDir, 'packager')
        ext.packagerArchDir = new File(ext.packagerBaseDir, PackagerUtils.archIdentifier())
        ext.releaseBasePath = Paths.get(ext.packagerBaseDir.toURI())
                .resolve('release')
                .resolve(PackagerUtils.archIdentifier())
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

