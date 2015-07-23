package org.deku.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
public class PackagerTask extends DefaultTask {
    // Task properties
    String group = "packager"

    // Optional properties, reasonable defaults
    String packageDescription
    String packageName = project.name
    String mainClassName = project.mainClassName
    /** Full path to main jar */
    String mainJar
    /** List of files/full paths of jars to include. Defaults to the project's configurations.compile.files */
    def jars = project.configurations.compile.files

    // Mandatory properties
    String title

    @TaskAction
    def packagerDeploy() {
        if (!packageDescription)
            packageDescription = title

        def jvm = org.gradle.internal.jvm.Jvm.current()
        def jdk_home = jvm.javaHome
        def jre_home = jvm.jre.homeDir

        println "JDK home [${jdk_home}]"
        println "JRE home [${jre_home}]"

        def packagerDir = new File(project.buildDir, 'packager')
        def packagerLibsDir = new File(packagerDir, 'libs')
        if (!packagerDir.deleteDir())
            throw new IOException("Could not remove packager dir");
        packagerDir.mkdirs()
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
                    "-outdir", packagerDir,
                    "-outfile", project.name,
                    "-srcdir", packagerLibsDir,
                    "-appclass", this.mainClassName,
                    "-Bruntime=${jre_home}",
                    (this.mainJar) ? "-BmainJar=${new File(this.mainJar).getName()}" : ""
        }
    }
}