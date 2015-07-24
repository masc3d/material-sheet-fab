package org.deku.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

abstract class PackagerTask extends DefaultTask {
    def String group = "packager"
    def packagerBaseDir = new File(project.buildDir, 'packager')
}

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
public class PackagerDeployTask extends PackagerTask {
    def String packageDescription
    def String packageName = project.name
    def String mainClassName = project.mainClassName
    /** Full path to main jar */
    def String mainJar
    /** List of files/full paths of jars to include. Defaults to the project's configurations.compile.files */
    def jars = project.configurations.compile.files
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

        def packagerLibsDir = new File(packagerBaseDir, 'libs')
        if (!packagerBaseDir.deleteDir())
            throw new IOException("Could not remove packager dir");
        packagerBaseDir.mkdirs()
        packagerLibsDir.mkdirs()

        println "Gathering jars"
        def sourceJars = jars.collect()
        if (this.mainJar)
            sourceJars += this.mainJar
        project.copy {
            from sourceJars
            into packagerLibsDir
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
    def releaseBasePath = new File(this.packagerBaseDir, "release")
    def bundlePath = Paths.get(this.packagerBaseDir.toString()).resolve("bundles").resolve(project.name)
}

public class PackagerReleaseJarsTask extends PackagerReleaseTask {

    @TaskAction
    def packagerReleaseJars() {
        println "Bundle path [${this.bundlePath}]"
        println "Release base path [${this.releaseBasePath}]"

        this.releaseBasePath.mkdirs()

        project.copy {
            from this.bundlePath.toFile()
            into this.releaseBasePath
        }
    }
}