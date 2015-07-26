package org.deku.gradle

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Base class for all packager tasks
 */
abstract class PackagerTask extends DefaultTask {
    def String group = "packager"
    protected def packagerBaseDir = Paths.get(project.buildDir.toURI()).resolve('packager')
    protected def packagerArchDir = packagerBaseDir.resolve(PackagerUtils.archIdentifier())
}

/**
 * Base class for all packager release tasks
 */
abstract class PackagerReleaseTask extends PackagerTask {
    protected def bundlePath = this.packagerArchDir.resolve('bundles').resolve(project.name)
    def releaseBasePath = this.packagerBaseDir.resolve('release').resolve(PackagerUtils.archIdentifier())
}

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
public class PackagerBundleTask extends PackagerTask {
    /** Full path to main jar */
    protected def String mainJar = project.tasks.jar.archivePath
    protected def String mainClassName = project.mainClassName
    /** List of files/full paths of jars to include. Defaults to the project's configurations.compile.files */
    protected def jars = project.configurations.compile.files + [this.mainJar]

    def String packageName = project.name
    def String packageDescription
    /** Jvm runtime options */
    def jvmOptions

    // Mandatory properties
    def String title

    @TaskAction
    def packagerDeploy() {
        if (!packageDescription)
            packageDescription = title

        def jvm = org.gradle.internal.jvm.Jvm.current()
        def jdk_home = jvm.javaHome
        def jre_home = jvm.jre.homeDir

        println "JDK home [${jdk_home}]"
        println "JRE home [${jre_home}]"

        println "Gathering jars"
        // Create libs dir for gathering
        def packagerLibsDir = this.packagerArchDir.resolve('libs')
        if (!packagerBaseDir.toFile().deleteDir())
            throw new IOException("Could not remove packager dir");
        Files.createDirectories(packagerLibsDir)
        // Copy
        project.copy {
            from this.jars
            into packagerLibsDir.toFile()
        }

        println "Building package"
        project.exec {
            environment JAVA_HOME: jdk_home
            commandLine "${jdk_home}/bin/javapackager",
                    "-deploy",
                    "-native", "image",
                    "-title", this.title,
                    "-description", this.packageDescription,
                    "-name", this.packageName,
                    "-outdir", this.packagerArchDir,
                    "-outfile", project.name,
                    "-srcdir", packagerLibsDir,
                    "-appclass", this.mainClassName,
                    "-Bruntime=${jre_home}",
                    (this.mainJar) ? "-BmainJar=${new File(this.mainJar).getName()}" : "",
                    (this.jvmOptions) ? "-BjvmOptions=${this.jvmOptions}" : ""

            // Debug: print command line
            // println String.join(" ", commandLine)
        }
    }
}

public class PackagerReleaseBundleTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseAll() {
        println "Bundle path [${this.bundlePath}]"
        println "Release base path [${this.releaseBasePath}]"

        if (!Files.exists(this.releaseBasePath))
            throw new IOException("Release base path [${this.releaseBasePath}] doesn't exist")

        // Remove content of release dir, preserving metadata directories (eg. .git)
        Files.walk(releaseBasePath, 1)
                .filter({ it -> !it.equals(releaseBasePath) && !it.getFileName().toString().equalsIgnoreCase(".git") })
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

        println "Copying bundle"
        project.copy {
            from this.bundlePath.toFile()
            into this.releaseBasePath.toFile()
        }
    }
}

public class PackagerReleaseJarsTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseJars() {
        println "Release base path [${this.releaseBasePath}]"

        def jarDestinationPath = this.releaseBasePath.resolve('app')
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

public class PackagerUtils {
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
    }
}