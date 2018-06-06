package sx.packager.gradle

import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.agentproxy.ConnectorFactory
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import com.jcraft.jsch.agentproxy.connector.PageantConnector
import org.apache.commons.compress.archivers.sevenz.SevenZMethod
import org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils
import sx.packager.Bundle
import sx.packager.BundleRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevObject
import org.eclipse.jgit.revwalk.RevTag
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.submodule.SubmoduleWalk
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.TagOpt
import org.eclipse.jgit.util.FS
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.tukaani.xz.LZMA2Options
import sx.platform.OperatingSystem
import sx.platform.PlatformId

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
class BuildNativeBundleTask extends Task {
    String group = GROUP_PACKAGER_NATIVE

    // Optional extension
    String packageName
    String packageDescription
    /** Jvm runtime options */
    def jvmOptions

    @TaskAction
    packagerDeploy() {
        if (!this.extension.title)
            throw new IllegalArgumentException("Title cannot be empty")

        // Prepare parameters
        if (!this.packageName)
            this.packageName = this.extension.bundleName
        if (!this.packageDescription)
            this.packageDescription = this.extension.title

        def packagerPlatformDir = this.getPackagerPlatformDir()
        def packagerPlatformBundlesDir = this.getPackagerPlatformBundlesDir()

        def mainJar = this.getMainJar()
        def mainClassName = this.getMainClassName();
        def jars = this.getProjectJars()
        def osxIcon = this.extension.osxIcon
        def windowsIcon = this.extension.windowsIcon

        // JDK/JRE
        def jvm = org.gradle.internal.jvm.Jvm.current()
        def jdk_home = jvm.javaHome
        // With jdk9, jre won't be available, thus reverting to jdk home
        def jre_home = jvm.jre?.homeDir ?: jdk_home
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
                    "-outdir", packagerPlatformBundlesDir,
                    "-outfile", this.extension.bundleName,
                    "-srcdir", packagerLibsDir,
                    "-appclass", mainClassName,
                    "-Bruntime=${jre_home}"

            if (SystemUtils.IS_OS_MAC && osxIcon)
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
class ReleaseNativeBundleTask extends ReleaseTask {
    def String group = GROUP_PACKAGER_NATIVE

    @TaskAction
    packagerReleaseAll() {
        def thisPlatform = PlatformId.current()

        if (this.extension.operatingSystems != null &&
                !this.extension.operatingSystems.contains(thisPlatform.operatingSystem)) {

            println "Skipping native bundle release for [${project.name}] on [${thisPlatform.operatingSystem.name()}]"
            return
        }

        def releasePlatformPath = this.getReleasePlatformPath()

        def packagerPlatformBundlesDir = this.getPackagerPlatformBundlesDir()

        def packagerBundlePath = Paths.get(packagerPlatformBundlesDir.toURI())
                .resolve(SystemUtils.IS_OS_MAC ? "" : this.extension.bundleName)
                .toFile()

        if (!packagerBundlePath.exists())
            throw new IOException("Bundle release path [${packagerBundlePath}] doesn't exist")

        if (!releasePlatformPath.exists())
            releasePlatformPath.mkdirs()
        else {
            // Remove content of release dir, preserving metadata directories (eg. .git)
            Files.walk(Paths.get(releasePlatformPath.toURI()), 1)
                    .filter({ it -> !it.equals(releasePlatformPath.toPath()) && !it.getFileName().toString().equalsIgnoreCase(".git") })
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

        println "Copying bundle [${packagerBundlePath}] -> [${releasePlatformPath}]"
        project.copy {
            from packagerBundlePath
            into releasePlatformPath
        }

        // TODO: workaround for bug in jdk 1.8.0_60, where jars are not picked up when building bundle
        // Jars are already missing in packager bundle dir. This worked fine with jdk 1.8.0_51
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            def releaseBundleJarPath = this.getReleaseBundle(PlatformId.current()).jarPath
            println "Copying jars -> [${releaseBundleJarPath}]"
            project.copy {
                from this.getProjectJars()
                into releaseBundleJarPath
            }
        }

        this.copySupplementalDirs(PlatformId.current())

        println "Creating bundle manifest"
        Bundle.create(
                this.getReleasePlatformPath(),
                this.extension.bundleName,
                PlatformId.current(),
                Bundle.Version.parse(this.extension.version))
    }
}

/**
 * Release update task
 * Updates native bundle with current jars and supplementals
 */
class ReleaseUpdateTask extends ReleaseTask {
    @TaskAction
    packagerReleaseJars() {
        def releasePath = this.getReleasePath()

        // Release jars for all architectures which are present within the release path for this project
        Files.walk(Paths.get(releasePath.toURI()), 1)
                .filter({ !it.toString().equals(releasePath.toString()) && it.toFile().isDirectory() })
                .each {

            def File releasePlatformPath = it.toFile()
            def PlatformId platformId = PlatformId.parse(releasePlatformPath.name)
            def releaseBundle = Bundle.load(this.getReleasePlatformPath(platformId))

            // Java version check here to prevent updating jars of a native bundle built with a different version
            if (!releaseBundle.javaVersion.equals(SystemUtils.JAVA_VERSION))
                throw new IllegalStateException("Java version of bundle [${releaseBundle}] does not match currently active java version [${SystemUtils.JAVA_VERSION}]")

            println "Updating jars and supplementals for [${platformId}]"

            def releaseBundleJarPath = releaseBundle.jarPath

            println "Jar destination path [${releaseBundleJarPath}]"

            if (!releaseBundleJarPath.exists())
                throw new IOException("Release jar destination path [${releaseBundleJarPath}] doesn't exist")

            // Remove jar files from jar destination path
            println "Removing all jars from [${releaseBundleJarPath}]"
            Files.walk(Paths.get(releaseBundleJarPath.toURI()), 1)
                    .filter { p -> Files.isRegularFile(p) && p.toString().toLowerCase().endsWith(".jar") }
                    .each { p ->
                Files.delete(p)
            }

            println "Copying jars -> [${releaseBundleJarPath}]"
            project.copy {
                from this.getProjectJars()
                into releaseBundleJarPath
            }

            this.copySupplementalDirs(platformId)

            // Update packager configuration file (main jar name, class path, start class)
            println("Updating bundle configuration")
            def bundleConfig = releaseBundle.configuration
            bundleConfig.appMainJar = this.getMainJar().getName()
            bundleConfig.appVersion = this.extension.version
            bundleConfig.appClassPath = this.getProjectJars().stream().map { it.getName() }.collect()
            bundleConfig.appMainclass = project.mainClassName
            bundleConfig.save()

            println "Creating bundle manifest"
            def bundle = Bundle.create(
                    this.getReleasePlatformPath(platformId),
                    this.extension.bundleName,
                    platformId,
                    Bundle.Version.parse(this.extension.version))

            bundle.makeExecutable()
        }
    }
}

/**
 * Release self extracting archive/installer task
 */
class ReleaseSfxTask extends ReleaseTask {
    @TaskAction
    packagerReleaseSfx() {
        if (this.extension.createSelfExtractingArchive) {
            // Release jars for all architectures which are present within the release path for this project
            Files.walk(Paths.get(releasePath.toURI()), 1)
                    .filter({ !it.toString().equals(releasePath.toString()) && it.toFile().isDirectory() })
                    .each {

                def File releasePlatformPath = it.toFile()
                def PlatformId platformId = PlatformId.parse(releasePlatformPath.name)

                if (platformId.operatingSystem == OperatingSystem.WINDOWS) {
                    def sfxPath = project.projectDir.toPath().resolve("sfx").resolve("win").toFile()
                    if (!sfxPath.exists()) {
                        logger.warn("Skipping sfx creation. Path [${sfxPath}] does not exist.")
                        return
                    }

                    println "Creating self extracting archive(s) for ${this.extension.bundleName}, ${platformId}"

                    // Archive content path
                    def archiveContentPath = this.getReleasePlatformPath(platformId)
                    def nioArchiveContentPath = archiveContentPath.toPath()

                    // Prepare build and release destinations
                    def archiveDirectoryName = "${this.extension.bundleName}-sfx"
                    def buildArchivePath = new File(this.extension.packagerBaseDir, archiveDirectoryName)
                    buildArchivePath.mkdirs()
                    def releaseArchivePath = new File(this.extension.releaseBasePath, archiveDirectoryName)
                    releaseArchivePath.mkdirs()

                    def buildArchiveName = "${this.extension.bundleName}-${this.extension.version}-${platformId}.7z"
                    def buildArchive = new File(buildArchivePath, buildArchiveName)

                    buildArchive.delete()

                    // Archive contents
                    def byte[] buffer = new byte[8 * 1024 * 1024]
                    def lzma2Options = new LZMA2Options()
                    lzma2Options.preset = 1
                    def lzma2Method = new SevenZMethodConfiguration(SevenZMethod.LZMA2, lzma2Options)

                    def szOut = new SevenZOutputFile(buildArchive)
                    try {
                        szOut.setContentMethods(Collections.singletonList(lzma2Method))
                        Files.walk(nioArchiveContentPath)
                                .filter { p -> !p.equals(nioArchiveContentPath) }
                                .each { p ->
                            def entryName = nioArchiveContentPath.relativize(p).toString()
                            def szEntry = szOut.createArchiveEntry(p.toFile(), entryName)
                            szOut.putArchiveEntry(szEntry)
                            if (Files.isRegularFile(p)) {
                                def is = p.newInputStream()
                                try {
                                    int length
                                    while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                                        szOut.write(buffer, 0, length)
                                    }
                                } finally {
                                    is.close()
                                }
                            }
                            szOut.closeArchiveEntry()
                        }
                    } finally {
                        szOut.close()
                    }

                    // Join files to self executable
                    def CFG_EXTENSION = ".cfg"
                    Files.walk(sfxPath.toPath())
                            .filter { p -> Files.isRegularFile(p) && p.fileName.toString().endsWith(CFG_EXTENSION) }
                            .each { p ->

                        def configName = p.fileName.toString()
                        configName = configName.substring(0, configName.length() - CFG_EXTENSION.length())
                        def releaseExecutable = new File(releaseArchivePath, "${this.extension.bundleName}-${this.extension.version}-${configName}-${platformId}.exe")

                        def os = releaseExecutable.newOutputStream()
                        try {
                            [new File(sfxPath, "7zsd.sfx"), p.toFile(), buildArchive].each {
                                def is = it.newInputStream()
                                try {
                                    IOUtils.copyLarge(is, os, buffer)
                                } finally {
                                    is.close()
                                }
                            }
                        } finally {
                            os.close()
                        }
                    }
                }
            }
        }
    }
}
