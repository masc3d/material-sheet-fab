package org.deku.gradle

import org.apache.commons.lang3.SystemUtils
import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListTagCommand
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.api.TagCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
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
}

/**
 * Base class for all packager release tasks
 */
abstract class PackagerReleaseTask extends PackagerTask {
    /**
     * Builds a release path based on project name and arch identitier
     * @param basePath
     * @return
     */
    def File buildReleasePath(File basePath) {
        return Paths.get(basePath.toURI())
                .resolve(project.name)
                .resolve(PackagerUtils.archIdentifier())
                .toFile()
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

        def packagerLibsDir = new File(this.extension.packagerArchDir, 'libs')

        println "Gathering jars -> [${packagerLibsDir}]"

        // Create libs dir for gathering
        packagerLibsDir.mkdirs()

        // Copy
        project.copy {
            from jars
            into packagerLibsDir
        }

        println "Creating bundle -> [${this.extension.packagerArchDir}]"
        project.exec {
            environment JAVA_HOME: jdk_home
            commandLine "${jdk_home}/bin/javapackager",
                    "-deploy",
                    "-native", "image",
                    "-title", this.extension.title,
                    "-description", this.packageDescription,
                    "-name", this.packageName,
                    "-outdir", this.extension.packagerArchDir,
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
        def releaseBasePath = this.extension.releaseBasePath
        def releasePath = this.buildReleasePath(releaseBasePath)

        def bundlePath = Paths.get(this.extension.packagerArchDir.toURI())
                .resolve('bundles')
                .resolve(SystemUtils.IS_OS_MAC_OSX ? "" : project.name)
                .toFile()

        if (!bundlePath.exists())
            throw new IOException("Bundle path [${bundlePath}] doesn't exist")

        if (!releasePath.exists())
            releasePath.mkdirs()
        else {
            // Remove content of release dir, preserving metadata directories (eg. .git)
            Files.walk(Paths.get(releasePath.toURI()), 1)
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
            from bundlePath
            into releasePath
        }
    }
}

/**
 * Release jars task
 */
class PackagerReleaseJarsTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseJars() {
        def releaseBasePath = this.extension.releaseBasePath
        def releasePath = this.buildReleasePath(releaseBasePath)
        def jarDestinationPath

        // Add path to jars within packager release bundle
        if (SystemUtils.IS_OS_MAC_OSX) {
            jarDestinationPath = Paths.get(releasePath.toURI())
                    .resolve(project.name + '.app')
                    .resolve('Contents')
                    .resolve('Java')
                    .toFile()
        } else {
            jarDestinationPath = new File(releasePath, 'app')
        }

        println "Release base path [${releaseBasePath}]"
        println "Release path [${releasePath}]"
        println "Jar destination path [${jarDestinationPath}]"

        if (!jarDestinationPath.exists())
            throw new IOException("Release jar destination path [${jarDestinationPath}] doesn't exist")

        // Remove jar files from jar destination path
        println "Removing all jars from [${jarDestinationPath}]"
        Files.walk(Paths.get(jarDestinationPath.toURI()), 0)
                .filter({ it -> it.toString().toLowerCase().endsWith(".jar") })
                .each { Files.delete(it) }

        println "Copying jars -> [${jarDestinationPath}]"
        project.copy {
            from this.getProjectJars()
            into jarDestinationPath
        }
    }
}

/**
 * Release push task for tagging and pushing a version to remote release repo
 */
class PackagerReleasePushTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleasePushTask() {
        def repo = FileRepositoryBuilder.create(new File(this.buildReleasePath(this.extension.releaseBasePath), ".git"))

        println "Pushing release ${project.name}-${project.version}"

        println "Determining repo status"
        def sc = new StatusCommand(repo)
        Status status = sc.call()
        if (status.untracked.isEmpty())
            throw new IllegalStateException("No untracked changes")

        println "Checking tags"
        def lt = new ListTagCommand(repo)
        List<Ref> refs = lt.call()
        refs.each { println it }

        println "Adding changes to index"
        def ac = new AddCommand(repo)
        ac.addFilepattern(".")
        ac.call()

        println "Committing changes"
        def cc = new CommitCommand(repo)
        cc.message = project.version
        cc.call()

        println "Creating tag ${project.version}"
        def tc = new TagCommand(repo)
        tc.name = project.version
        tc.call()
    }
}

