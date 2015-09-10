package org.deku.gradle

import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.build.Artifact
import org.deku.leoz.build.ArtifactRepository
import org.deku.leoz.build.ArtifactRepositoryFactory
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import sx.platform.OperatingSystem
import sx.platform.PlatformId

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * Base class for all packager tasks
 */
abstract class PackagerTask extends DefaultTask {
    /** Group for all packager tasks */
    def String group = "packager"
    /** Plugin extension used for extension within build.gradle scope */
    def PackagerPluginExtension extension

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

    /**
     * Build packager platform/arch path for current system
     * @return
     */
    protected def File getPackagerPlatformDir() {
        return new File(this.extension.packagerBaseDir, PlatformId.current().toString())
    }
}

/**
 * Base class for all packager release tasks
 */
abstract class PackagerReleaseTask extends PackagerTask {
    /**
     * Build release path based on project name
     * @return
     */
    def File getReleasePath() {
        return new File(this.extension.getReleaseBasePath(), project.name)
    }

    /**
     * Builds a release path for current project and specific platform/arch
     * @param basePath Release base path
     * @param platformId Platform/arch
     * @return
     */
    def File getReleasePlatformPath(PlatformId platformId) {
        return new File(this.getReleasePath(), platformId.toString())
    }

    /**
     * Builds a release path for current project and platform/arch
     * @param basePath Release base path
     * @return
     */
    def File getReleasePlatformPath() {
        return this.getReleasePlatformPath(PlatformId.current())
    }

    /**
     * Gets a release supplmental path
     * @param platformId Platform
     * @param relativePath Relative sub path
     * @return
     */
    protected def getReleaseSupplementalPath(PlatformId platformId, File relativePath) {
        def dst
        if (SystemUtils.IS_OS_MAC_OSX) {
            dst = Paths.get(this.getReleasePlatformPath(platformId).toURI())
                    .resolve("${project.name}.app")
                    .resolve('Contents')
                    .resolve(relativePath.toString())
                    .toFile()
        } else {
            dst = new File(this.getReleasePlatformPath(platformId), relativePath.toString())
        }
        return dst
    }

    /**
     * Builds jar destination path for specific platform/arch
     * @param basePath
     * @return
     */
    def File getReleaseJarPath(PlatformId platformId) {
        def File jarDestinationPath

        def File releasePlatformPath = this.getReleasePlatformPath(platformId)

        // Add path to jars within packager release bundle
        if (platformId.operatingSystem == OperatingSystem.OSX) {
            jarDestinationPath = Paths.get(releasePlatformPath.toURI())
                    .resolve(project.name + '.app')
                    .resolve('Contents')
                    .resolve('Java')
                    .toFile()
        } else {
            jarDestinationPath = new File(releasePlatformPath, 'app')
        }
        return jarDestinationPath
    }

    /**
     * Copy plain plain supplemental dirs
     * @param platformId
     * @return
     */
    protected def copySupplementalDirs(PlatformId platformId) {
        this.extension.getSupplementalDirs().each {
            it -> println it.key
        }
    }

    /**
     * Copy platform specific supplemental dirs
     * @param platformId
     * @return
     */
    protected def copySupplementalPlatformDirs(PlatformId platformId) {
        def dstDirs = (this.extension.getSupplementalPlatformDirs().values() + this.extension.getSupplementalDirs().values())

        dstDirs.each { it ->
            def path = this.getReleaseSupplementalPath(platformId, it)
            if (path.exists())
                path.deleteDir()
        }

        this.extension.getSupplementalDirs().each { it ->
            def src = it.key
            def dst = this.getReleaseSupplementalPath(platformId, it.value)

            println "Copying supplemental dir [${src}] -> [${dst}]"
            project.copy {
                from src
                into dst
            }
        }

        this.extension.getSupplementalPlatformDirs().each { it ->
            def src = new File(it.key, PlatformId.current().toString())
            def dst = this.getReleaseSupplementalPath(platformId, it.value)

            println "Copying supplemental platform dir [${src}] -> [${dst}]"
            project.copy {
                from src
                into dst
            }
        }
    }
}

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
class PackagerBundleTask extends PackagerTask {
    // Optional extension
    def String packageName
    def String packageDescription
    /** Jvm runtime options */
    def jvmOptions

    @TaskAction
    def packagerDeploy() {
        if (!this.extension.title)
            throw new IllegalArgumentException("Titla cannot be empty")

        // Prepare parameters
        if (!this.packageName)
            this.packageName = project.name
        if (!this.packageDescription)
            this.packageDescription = this.extension.title

        def packagerPlatformDir = this.getPackagerPlatformDir()

        def mainJar = this.getMainJar()
        def mainClassName = this.getMainClassName();
        def jars = this.getProjectJars()
        def osxIcon = this.extension.osxIcon
        def windowsIcon = this.extension.windowsIcon

        // JDK/JRE
        def jvm = org.gradle.internal.jvm.Jvm.current()
        def jdk_home = jvm.javaHome
        def jre_home = jvm.jre.homeDir
        println "JDK home [${jdk_home}]"
        println "JRE home [${jre_home}]"

        if (!this.extension.packagerBaseDir.deleteDir())
            throw new IOException("Could not remove packager dir");

        def packagerLibsDir = new File(packagerPlatformDir, 'libs')

        println "Gathering jars -> [${packagerLibsDir}]"

        // Create libs dir for gathering
        packagerLibsDir.mkdirs()

        // Copy
        project.copy {
            from jars
            into packagerLibsDir
        }

        println "Creating bundle -> [${packagerPlatformDir}]"
        project.exec {
            environment JAVA_HOME: jdk_home
            commandLine "${jdk_home}/bin/javapackager",
                    "-deploy",
                    "-native", "image",
                    "-title", this.extension.title,
                    "-description", this.packageDescription,
                    "-name", this.packageName,
                    "-outdir", packagerPlatformDir,
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
        def releasePlatformPath = this.getReleasePlatformPath()

        def packagerPlatformDir = this.getPackagerPlatformDir()

        def bundlePath = Paths.get(packagerPlatformDir.toURI())
                .resolve('bundles')
                .resolve(SystemUtils.IS_OS_MAC_OSX ? "" : project.name)
                .toFile()

        if (!bundlePath.exists())
            throw new IOException("Bundle path [${bundlePath}] doesn't exist")

        if (!releasePlatformPath.exists())
            releasePlatformPath.mkdirs()
        else {
            // Remove content of release dir, preserving metadata directories (eg. .git)
            Files.walk(Paths.get(releasePlatformPath.toURI()), 1)
                    .filter({ it -> !it.equals(releasePlatformPath) && !it.getFileName().toString().equalsIgnoreCase(".git") })
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

        println "Copying bundle [${bundlePath}] -> [${releasePlatformPath}]"
        project.copy {
            from bundlePath
            into releasePlatformPath
        }

        this.copySupplementalDirs(PlatformId.current())
        this.copySupplementalPlatformDirs(PlatformId.current())

        println "Creating artifact/manifest"
        Artifact.create(releasePlatformPath, project.name, Artifact.Version.parse(project.version))
    }
}

/**
 * Release jars task
 */
class PackagerReleaseJarsTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseJars() {
        def releasePath = this.getReleasePath()

        // Release jars for all architectures which are present within the release path for this project
        Files.walk(Paths.get(releasePath.toURI()), 1)
                .filter({ !it.toString().equals(releasePath.toString()) && it.toFile().isDirectory() })
                .each {

            def File releasePlatformPath = it.toFile()
            def PlatformId platformId = PlatformId.parse(releasePlatformPath.name)

            println "Releasing jars and binaries for [${platformId}]"

            def jarDestinationPath = this.getReleaseJarPath(platformId)

            println "Jar destination path [${jarDestinationPath}]"

            if (!jarDestinationPath.exists())
                throw new IOException("Release jar destination path [${jarDestinationPath}] doesn't exist")

            // Remove jar files from jar destination path
            println "Removing all jars from [${jarDestinationPath}]"
            Files.walk(Paths.get(jarDestinationPath.toURI()), 1)
                    .filter { p -> Files.isRegularFile(p) && p.toString().toLowerCase().endsWith(".jar") }
                    .each { p ->
                Files.delete(p)
            }

            println "Copying jars -> [${jarDestinationPath}]"
            project.copy {
                from this.getProjectJars()
                into jarDestinationPath
            }

            this.copySupplementalDirs(platformId)
            this.copySupplementalPlatformDirs(platformId)

            println "Creating artifact/manifest"
            Artifact.create(releasePlatformPath, project.name, Artifact.Version.parse(project.version))
        }
    }
}

/**
 * Release push task for tagging and pushing a version to remote release repo
 */
class PackagerReleasePushTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleasePushTask() {
        ArtifactRepository ar = ArtifactRepositoryFactory.INSTANCE$.stagingRepository(project.name)

        ar.upload(
                this.getReleasePath(),
                { s, d -> println("Synchronizing [${s}] -> [${d}]") },
                { f -> println("Uploading [${f.path}]") }
        )
    }
}

class PackagerReleasePullTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleasePullTask() {
        def releasePath = this.getReleasePath()

        def version = Artifact.Version.parse(project.version)
        ArtifactRepository ar = ArtifactRepositoryFactory.INSTANCE$.stagingRepository(project.name)

        def remoteVersions = ar.listVersions()
                .stream()
                .filter { v -> v.compareTo(version) <= 0 }
                .sorted().collect(Collectors.toList()).reverse()

        if (remoteVersions.size() == 0)
            throw new IllegalStateException("No remote versions <= ${version}")

        ar.download(remoteVersions.get(0), releasePath, true)
    }
}

