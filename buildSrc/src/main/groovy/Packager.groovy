package org.deku.gradle

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Plugin extension class, used for configuration within build.gradle
 */
class PackagerPluginExtension {
    def String title
    def File releaseBasePath
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

/**
 * Base class for all packager tasks
 */
abstract class PackagerTask extends DefaultTask {
    /** Group for all packager tasks */
    def String group = "packager"
    /** Plugin extension used for configuration within build.gradle scope */
    def PackagerPluginExtension configuration

    // Common locations
    protected def packagerBaseDir = Paths.get(project.buildDir.toURI()).resolve('packager')
    protected def packagerArchDir = packagerBaseDir.resolve(PackagerUtils.archIdentifier())
}

/**
 * Base class for all packager release tasks
 */
abstract class PackagerReleaseTask extends PackagerTask {
    // Common locations
    protected def bundlePath = this.packagerArchDir.resolve('bundles').resolve(project.name)
    protected def releaseBasePath = this.packagerBaseDir.resolve('release').resolve(PackagerUtils.archIdentifier())

    protected def createReleasePath() {
        return this.releaseBasePath.resolve("${project.name}-${PackagerUtils.archIdentifier()}")
    }
}

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
class PackagerBundleTask extends PackagerTask {
    def PackagerPluginExtension configuration
    // Optional configuration
    def String packageName
    def String packageDescription
    /** Jvm runtime options */
    def jvmOptions

    public PackagerBundleTask() {
        //jars = project.configurations.compile.files + [this.mainJar]
    }

    @TaskAction
    def packagerDeploy() {
        if (!this.configuration.title)
            throw new IllegalArgumentException("Titla cannot be empty")

        // Prepare parameters
        if (!this.packageName)
            this.packageName = project.name
        if (!this.packageDescription)
            this.packageDescription = this.configuration.title

        def mainJar = project.tasks.jar.archivePath
        def mainClassName = project.mainClassName
        def jars = project.configurations.compile.files + [mainJar]

        // JDK/JRE
        def jvm = org.gradle.internal.jvm.Jvm.current()
        def jdk_home = jvm.javaHome
        def jre_home = jvm.jre.homeDir
        println "JDK home [${jdk_home}]"
        println "JRE home [${jre_home}]"

        println "Gathering jars"
        // Create libs dir for gathering
        def packagerLibsDir = this.packagerArchDir.resolve('libs')
        if (!this.packagerBaseDir.toFile().deleteDir())
            throw new IOException("Could not remove packager dir");
        Files.createDirectories(packagerLibsDir)
        // Copy
        project.copy {
            from jars
            into packagerLibsDir.toFile()
        }

        println "Building package"
        project.exec {
            environment JAVA_HOME: jdk_home
            commandLine "${jdk_home}/bin/javapackager",
                    "-deploy",
                    "-native", "image",
                    "-title", this.configuration.title,
                    "-description", this.packageDescription,
                    "-name", this.packageName,
                    "-outdir", this.packagerArchDir,
                    "-outfile", project.name,
                    "-srcdir", packagerLibsDir,
                    "-appclass", mainClassName,
                    "-Bruntime=${jre_home}",
                    (mainJar) ? "-BmainJar=${mainJar.getName()}" : "",
                    (this.jvmOptions) ? "-BjvmOptions=${this.jvmOptions}" : ""

            // Debug: print command line
            // println String.join(" ", commandLine)
        }
    }
}

/**
 * Release bundle task
 */
class PackagerReleaseBundleTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseAll() {
        if (this.configuration.releaseBasePath)
            this.releaseBasePath = Paths.get(this.configuration.releaseBasePath.toURI())

        def releasePath = this.createReleasePath()

        println "Bundle path [${this.bundlePath}]"
        println "Release base path [${this.releaseBasePath}]"
        println "Release path [${releasePath}]"

        if (!Files.exists(releasePath))
            Files.createDirectories(releasePath)
        else {
            // Remove content of release dir, preserving metadata directories (eg. .git)
            Files.walk(releasePath, 1)
                    .filter({ it -> !it.equals(releasePath) && !it.getFileName().toString().equalsIgnoreCase(".git") })
                    .each {
                File f = it.toFile()
                boolean success
                if (f.isDirectory())
                    success = f.deleteDir()
                else
                    success = f.delete()
                if (!success)
                    throw new IOException("Could not remove [{$it}]")
            }
        }

        println "Copying bundle"
        project.copy {
            from this.bundlePath.toFile()
            into releasePath.toFile()
        }
    }
}

/**
 * Release jars task
 */
class PackagerReleaseJarsTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseJars() {
        if (this.configuration.releaseBasePath)
            this.releaseBasePath = Paths.get(this.configuration.releaseBasePath.toURI())

        def releasePath = this.createReleasePath()
        def jarDestinationPath = releasePath.resolve('app')

        println "Release base path [${this.releaseBasePath}]"
        println "Release path [${releasePath}]"
        println "Jar destination path [${jarDestinationPath}]"

        if (!Files.exists(jarDestinationPath))
            throw new IOException("Release jar destination path [${jarDestinationPath}] doesn't exist")

        // Remove jar files from jar destination path
        println "Removing all jars from [${jarDestinationPath}]"
        Files.walk(jarDestinationPath, 0)
                .filter({ it -> it.toString().toLowerCase().endsWith(".jar") })
                .each { Files.delete(it) }

        println "Gathering jars"
        project.copy {
            from project.configurations.compile.files + [project.tasks.jar.archivePath]
            into jarDestinationPath.toFile()
        }
    }
}

/**
 * Common packager utils
 */
class PackagerUtils {
    public static String archIdentifier() {
        String prefix = SystemUtils.IS_OS_WINDOWS ? "win"
                : SystemUtils.IS_OS_LINUX ? "linux"
                : SystemUtils.IS_OS_MAC_OSX ? "osx"
                : null;

        if (!prefix)
            throw IllegalStateException("Unsupported platform");

        switch (SystemUtils.OS_ARCH) {
            case "amd64": prefix += "64"; break;
            case "x86": prefix += "32"; break;
            default: throw IllegalStateException("Unsupported architecture");
        }

        return prefix
    }
}
