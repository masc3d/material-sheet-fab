package org.deku.gradle

import org.apache.commons.lang3.SystemUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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

    /**
     * Collection of all project jars (main jar + dependencies)
     * @return Project jars
     */
    protected def Set<File> getProjectJars() {
        return project.configurations.compile.files + [project.tasks.jar.archivePath]
    }

    /**
     * Project main jar
     * @return
     */
    protected def File getMainJar() {
        return project.tasks.jar.archivePath
    }

    /**
     * Main jar main class
     * @return
     */
    protected def String getMainClassName() {
        return project.mainClassName
    }
}

/**
 * Base class for all packager release tasks
 */
abstract class PackagerReleaseTask extends PackagerTask {
    // Common locations
    protected def Path releaseBasePath = this.packagerBaseDir.resolve('release').resolve(PackagerUtils.archIdentifier())

    def getReleasePath(Path basePath) {
        return basePath.resolve(project.name).resolve(PackagerUtils..archIdentifier())
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

    @TaskAction
    def packagerDeploy() {
        if (!this.configuration.title)
            throw new IllegalArgumentException("Titla cannot be empty")

        // Prepare parameters
        if (!this.packageName)
            this.packageName = project.name
        if (!this.packageDescription)
            this.packageDescription = this.configuration.title

        def mainJar = this.getMainJar()
        def mainClassName = this.getMainClassName();
        def jars = this.getProjectJars()
        def osxIcon = this.configuration.osxIcon
        def windowsIcon = this.configuration.windowsIcon

        // JDK/JRE
        def jvm = org.gradle.internal.jvm.Jvm.current()
        def jdk_home = jvm.javaHome
        def jre_home = jvm.jre.homeDir
        println "JDK home [${jdk_home}]"
        println "JRE home [${jre_home}]"

        if (!this.packagerBaseDir.toFile().deleteDir())
            throw new IOException("Could not remove packager dir");

        def packagerLibsDir = this.packagerArchDir.resolve('libs')

        println "Gathering jars -> [${packagerLibsDir}]"

        // Create libs dir for gathering
        Files.createDirectories(packagerLibsDir)

        // Copy
        project.copy {
            from jars
            into packagerLibsDir.toFile()
        }

        println "Creating bundle -> [${this.packagerArchDir}]"
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
                    "-Bruntime=${jre_home}"

            if (SystemUtils.IS_OS_MAC_OSX && osxIcon)
                commandLine += "-Bicon=${osxIcon}"

            if (SystemUtils.IS_OS_WINDOWS && windowsIcon)
                commandLine += "-Bicon=${windowsIcon}"

            if (mainJar)
                commandLine += "-BmainJar=${mainJar.getName()}"

            if (this.jvmOptions)
                commandLine += "-BjvmOptions=${this.jvmOptions}"

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

        def releasePath = this.getReleasePath(this.releaseBasePath)

        def bundlePath = this.packagerArchDir.resolve('bundles').resolve(
                SystemUtils.IS_OS_MAC_OSX ? "" : project.name)

        if (!Files.exists(bundlePath))
            throw new IOException("Bundle path [${bundlePath}] doesn't exist")

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

        println "Copying bundle [${bundlePath}] -> [${releasePath}]"
        project.copy {
            from bundlePath.toFile()
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

        def releasePath = this.getReleasePath(this.releaseBasePath)
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

        println "Copying jars -> [${jarDestinationPath}]"
        project.copy {
            from this.getProjectJars()
            into jarDestinationPath.toFile()
        }
    }
}
