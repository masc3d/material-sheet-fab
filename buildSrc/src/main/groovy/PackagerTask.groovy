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

    // Mandatory properties
    String title
    /** List of files/full paths of jars to include */
    def jars

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
        packagerDir.mkdirs()
        packagerLibsDir.mkdirs()

        println "Gathering jars"
        project.copy {
            from jars
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
                    "-Bruntime=${jre_home}"
        }
    }
}