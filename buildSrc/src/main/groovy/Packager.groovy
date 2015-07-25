package org.deku.gradle

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Paths

abstract class PackagerTask extends DefaultTask {
    protected def String group = "packager"
    protected def packagerBaseDir = Paths.get(project.buildDir.toURI()).resolve('packager')
}

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
public class PackagerDeployTask extends PackagerTask {
    /** Full path to main jar */
    protected def String mainJar = project.tasks.jar.archivePath
    protected def String mainClassName = project.mainClassName
    /** List of files/full paths of jars to include. Defaults to the project's configurations.compile.files */
    protected def jars = project.configurations.compile.files + [ this.mainJar ]

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

        Files.createDirectories(packagerBaseDir)

        println "Gathering jars"
        // Create libs dir for gathering
        def packagerLibsDir = this.packagerBaseDir.resolve('libs')
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
            commandLine "${jdk_home}/bin/javapackager",
                    "-deploy",
                    "-native", "image",
                    "-title", this.title,
                    "-description", this.packageDescription,
                    "-name", this.packageName,
                    "-outdir", packagerBaseDir,
                    "-outfile", project.name,
                    "-srcdir", packagerLibsDir,
                    "-appclass", this.mainClassName,
                    "-Bruntime=${jre_home}",
                    (this.mainJar) ? "-BmainJar=${new File(this.mainJar).getName()}" : "",
                    (this.jvmOptions) ? "-BjvmOptions=${this.jvmOptions}" : ""
        }
    }
}

abstract class PackagerReleaseTask extends PackagerTask {
    protected def bundlePath = this.packagerBaseDir.resolve('bundles').resolve(project.name)

    def releaseBasePath = this.packagerBaseDir.resolve('release')
}

public class PackagerReleaseAllTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseAll() {
        println "Bundle path [${this.bundlePath}]"
        println "Release base path [${this.releaseBasePath}]"

        if (!this.releaseBasePath.toFile().deleteDir())
            throw new IOException("Could not remove release base dir");
        Files.createDirectories(this.releaseBasePath)

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

        // Remove all jar files
        println "Removing all jars from [${jarDestinationPath}]"
        Files.walk(jarDestinationPath, 0)
                .filter( { it -> it.toString().endsWith(".jar") } )
                .each { Files.delete(it) }

        println "Gathering jars"
        project.copy {
            from project.configurations.compile.files + [ project.tasks.jar.archivePath ]
            into jarDestinationPath.toFile()
        }
    }
}