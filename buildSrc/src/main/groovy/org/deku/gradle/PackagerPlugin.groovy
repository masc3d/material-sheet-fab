package org.deku.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import sx.platform.PlatformId
import sx.rsync.Rsync

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

    /** Perform repository sanity checks */
    def Boolean checkRepository = true

    /** Create self extracting archive */
    def Boolean createSelfExtractingArchive = false

    /** Map of source -> (relative) destination directories to copy to bundle release directory */
    def supplementalDirs(LinkedHashMap<File, File> dirs) {
        mSupplementalDirs = dirs
    }

    /** Map of source dirs containing arch subdirs -> (relative) destination directories to copy to bundle release directory */
    def supplementalPlatformDirs(LinkedHashMap<File, File> dirs) {
        mSupplementalPlatformDirs = dirs
    }

    private def LinkedHashMap<File, File> mSupplementalDirs = new LinkedHashMap<>()
    public LinkedHashMap<File, File> getSupplementalDirs() {
        return mSupplementalDirs
    }

    private def LinkedHashMap<File, File> mSupplementalPlatformDirs = new LinkedHashMap<>()

    public LinkedHashMap<File, File> getSupplementalPlatformDirs() {
        return mSupplementalPlatformDirs
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
        project.tasks.create('buildNativeBundle', PackagerNativeBundleTask) {
            extension = ext
            //masc20160202. Seen sporadic/failures related to Compress Oops (heap too large)
            //jvmOptions = "-XX:+UseCompressedOops"
        }
        project.tasks.buildNativeBundle.dependsOn(project.tasks.jar)

        // Release bundle task
        project.tasks.create('releaseNativeBundle', PackagerReleaseNativeBundleTask) {
            extension = ext
        }
        project.tasks.releaseNativeBundle.dependsOn(project.tasks.buildNativeBundle)

        // Release jars task
        project.tasks.create('releaseUpdate', PackagerReleaseUpdateTask) {
            extension = ext
        }
        project.tasks.releaseUpdate.dependsOn(project.tasks.jar)

        project.tasks.create('releaseSfx', PackagerReleaseSfxTask) {
            extension = ext
        }
        project.tasks.releaseSfx.dependsOn(project.tasks.releaseUpdate)

        // Release push task
        project.tasks.create('releasePush', PackagerReleasePushTask) {
            extension = ext
        }
        project.tasks.releasePush.dependsOn(project.tasks.releaseUpdate)

        // Release pull task
        project.tasks.create('releasePull', PackagerReleasePullTask) {
            extension = ext
        }

        // Release clean task
        project.tasks.create('releaseClean', PackagerReleaseCleanTask) {
            extension = ext
        }

        // Initialize rsync
        Rsync.executable.file = Paths.get(project.project(':libs:sx-rsync').projectDir.toURI())
                .resolve('platform')
                .resolve(PlatformId.current().toString())
                .resolve('bin')
                .resolve('sx-rsync')
                .toFile();
    }
}

