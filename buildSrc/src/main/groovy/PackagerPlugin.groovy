package org.deku.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by n3 on 02-Aug-15.
 */
/**
 * Plugin extension class, used for configuration within build.gradle
 */
class PackagerPluginExtension {
    def String title
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
        // Add configuration extensino
        def config = new PackagerPluginExtension()
        project.extensions.packager = config

        // Bundle task
        project.tasks.create('packagerBundle', PackagerBundleTask) {
            configuration = config
            jvmOptions = "-XX:+UseCompressedOops"
        }
        project.tasks.packagerBundle.dependsOn(project.tasks.jar)

        // Release bundle task
        project.tasks.create('packagerReleaseBundle', PackagerReleaseBundleTask) {
            configuration = config
        }
        project.tasks.packagerReleaseBundle.dependsOn(project.tasks.packagerBundle)

        // Release jars task
        project.tasks.create('packagerReleaseJars', PackagerReleaseJarsTask) {
            configuration = config
        }
        project.tasks.packagerReleaseJars.dependsOn(project.tasks.jar)
    }
}

