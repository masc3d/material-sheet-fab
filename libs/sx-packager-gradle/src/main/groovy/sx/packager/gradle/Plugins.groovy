package sx.packager.gradle

import org.apache.commons.lang3.SystemUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import sx.packager.BundleRepository
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import sx.rsync.Rsync
import sx.ssh.SshTunnelProvider

import java.nio.file.Paths

/**
 * Plugin extension class, used for extension within build.gradle
 */
class PackagerPluginExtension {
    static class Android {
        /** Android build type */
        String buildType
        /** Android product flavor */
        String productFlavor
    }

    def Android androidExtension

    def String title

    /** Bundle name. Defaults to `project.name` */
    String bundleName = null
    /** Bundle version. Defaults to `project.name` (`android.defaultConfig.versionName` for `sx-packager-android`) */
    String version = null
    /** Path to .icns file */
    File osxIcon
    /** Path to .ico file */
    File windowsIcon
    /**
     * Optionallist of operating systems to build native bundles for when invoking `releaseNativeBundle.
     * If this parameter is omitted native bundle will be built for any os.
     * */
    List<OperatingSystem> operatingSystems = null

    /** Packager base directory */
    File packagerBaseDir
    /** Release directory base path */
    File releaseBasePath

    /** Native platform binary location */
    File nativePlatformDir
    /** Perform repository sanity checks */
    Boolean checkRepository = true
    /** Create self extracting archive */
    Boolean createSelfExtractingArchive = false

    /** SSH tunnel provider for establishing secure rsync connections */
    SshTunnelProvider sshTunnelProvider = null
    /** Bundle repository for push/pull*/
    BundleRepository bundleRepository = null

    /** Git repository root */
    File gitRoot = null

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

abstract class PackagerPlugin implements Plugin<Project> {
    protected PackagerPluginExtension packagerExtension
    protected PackagerPluginExtension.Android packagerAndroidExtension

    void apply(Project project) {

        // Add extension extension
        packagerExtension = project.extensions.create('packager', PackagerPluginExtension)
        packagerExtension.packagerBaseDir = new File(project.buildDir, 'packager')
        packagerExtension.releaseBasePath = Paths.get(packagerExtension.packagerBaseDir.toURI())
                .resolve('release')
                .toFile()

        packagerAndroidExtension = project.extensions.packager.extensions.create('android', PackagerPluginExtension.Android)
        packagerAndroidExtension.buildType = 'release'
        packagerAndroidExtension.productFlavor = ''

        packagerExtension.androidExtension = packagerAndroidExtension

        project.gradle.projectsEvaluated {
            // Initialize rsync
            if (SystemUtils.IS_OS_LINUX) {
                Rsync.executable.file = new File("/usr/bin/rsync")
            } else {
                if (packagerExtension.nativePlatformDir != null) {
                    Rsync.executable.file = packagerExtension.nativePlatformDir.toPath()
                            .resolve(PlatformId.current().toString())
                            .resolve('bin')
                            .resolve('sx-rsync')
                            .toFile();
                }
            }
        }
    }
}

/**
 * Packager plugin
 */
class JavaPackagerPlugin extends PackagerPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)

        project.tasks.create('buildNativeBundle', BuildNativeBundleTask) {
            extension = packagerExtension
        }

        // Release bundle task
        project.tasks.create('releaseNativeBundle', ReleaseNativeBundleTask) {
            extension = packagerExtension
        }
        project.tasks.releaseNativeBundle.dependsOn(project.tasks.buildNativeBundle)

        // Release jars task
        project.tasks.create('releaseUpdate', ReleaseUpdateTask) {
            extension = packagerExtension
        }

        project.tasks.create('releaseSfx', ReleaseSfxTask) {
            extension = packagerExtension
        }
        project.tasks.releaseSfx.dependsOn(project.tasks.releaseUpdate)

        // Release push task
        project.tasks.create('releasePush', ReleasePushTask) {
            extension = packagerExtension
        }
        project.tasks.releasePush.dependsOn(project.tasks.releaseUpdate)

        // Release pull task
        project.tasks.create('releasePull', ReleasePullTask) {
            extension = packagerExtension
        }

        // Release clean task
        project.tasks.create('releaseClean', ReleaseCleanTask) {
            extension = packagerExtension
        }

        project.gradle.projectsEvaluated {
            project.tasks.buildNativeBundle.dependsOn(project.tasks.jar)
            project.tasks.releaseUpdate.dependsOn(project.tasks.jar)
        }

        project.gradle.projectsEvaluated {
            if (this.packagerExtension.bundleName == null)
                this.packagerExtension.bundleName = project.name

            if (this.packagerExtension.version == null)
                this.packagerExtension.version = project.version

            if (this.packagerExtension.gitRoot == null)
                this.packagerExtension.gitRoot = project.rootDir
        }
    }
}

/**
 * Packager plugin
 */
class AndroidPackagerPlugin extends PackagerPlugin {
    @Override
    void apply(Project project) {
        super.apply(project)

        // Bundle task
        project.tasks.create('buildNativeBundle')

        // Release bundle task
        project.tasks.create('releaseNativeBundle', ReleaseNativeBundleAndroidTask) {
            extension = packagerExtension
        }
        project.tasks.releaseNativeBundle.dependsOn(project.tasks.buildNativeBundle)

        // Release push task
        project.tasks.create('releasePush', ReleasePushTask) {
            extension = packagerExtension
        }
        project.tasks.releasePush.dependsOn(project.tasks.releaseNativeBundle)

        project.tasks.create('releaseUpdate')
        project.tasks.releaseUpdate.dependsOn(project.tasks.releaseNativeBundle)

        // Release pull task
        project.tasks.create('releasePull', ReleasePullTask) {
            extension = packagerExtension
        }

        // Release clean task
        project.tasks.create('releaseClean', ReleaseCleanTask) {
            extension = packagerExtension
        }

        project.plugins.withType(JavaPlugin).whenPluginAdded {
            if (this.packagerExtension.bundleName == null)
                this.packagerExtension.bundleName = project.name
        }

        project.gradle.projectsEvaluated {

            if (this.packagerExtension.version == null)
                this.packagerExtension.version = project.android.defaultConfig.versionName
        }

        def assembleTaskName = "assemble${this.packagerAndroidExtension.productFlavor.capitalize()}${this.packagerAndroidExtension.buildType.capitalize()}"

        project.tasks.whenTaskAdded {
            if (it.name == assembleTaskName) {
                project.tasks.buildNativeBundle.dependsOn(it)
            }
        }
    }
}
