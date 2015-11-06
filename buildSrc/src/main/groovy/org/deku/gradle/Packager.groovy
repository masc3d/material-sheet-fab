package org.deku.gradle

import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.agentproxy.ConnectorFactory
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import org.apache.commons.compress.archivers.sevenz.SevenZMethod
import org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.config.BundleRepositoryConfiguration
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
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
 * Base class for all packager tasks
 */
abstract class PackagerTask extends DefaultTask {
    final GROUP_PACKAGER = 'packager'
    final GROUP_PACKAGER_NATIVE = 'packager-native'

    /** Group for all packager tasks */
    def String group = GROUP_PACKAGER
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
     * Builds path to bundle within platform release path
     * @param platformId Platform
     * @return
     */
    def File getReleasePlatformBundlePath(PlatformId platformId) {
        if (platformId.operatingSystem == OperatingSystem.OSX)
            return new File(this.getReleasePlatformPath(platformId), "${project.name}.app")
        else
            this.getReleasePlatformPath(platformId)
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
     * Builds path to bundle within platform release path for current platform/project
     * @return
     */
    def File getReleasePlatformBundlePath() {
        return this.getReleasePlatformBundlePath(PlatformId.current())
    }

    /**
     * Returns bundle for platform
     * @param platformId
     * @return
     */
    def Bundle getReleaseBundle(PlatformId platformId) {
        return new Bundle(
                this.getReleasePlatformBundlePath(platformId),
                project.name,
                Bundle.Version.parse(project.version),
                platformId)
    }

    /**
     * Gets a release supplmental path
     * @param platformId Platform
     * @param relativePath Relative sub path
     * @return
     */
    protected def getReleaseSupplementalPath(PlatformId platformId, File relativePath) {
        return new File(this.getReleaseBundle(platformId).contentPath, relativePath.toString())
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
            def src = new File(it.key, platformId.toString())
            def dst = this.getReleaseSupplementalPath(platformId, it.value)

            println "Copying supplemental platform dir [${src}] -> [${dst}]"
            project.copy {
                from src
                into dst
            }
        }
    }

    protected def createSelfExtractingArchive(PlatformId platformId) {
        if (this.extension.createSelfExtractingArchive) {
            if (platformId.operatingSystem == OperatingSystem.WINDOWS) {
                def sfxPath = project.projectDir.toPath().resolve("sfx").resolve("win").toFile()
                if (!sfxPath.exists()) {
                    logger.warn("Skipping sfx creation. Path [${sfxPath}] does not exist.")
                    return
                }

                println "Creating self extracting archive(s)"

                // Archive content path
                def archiveContentPath = this.getReleasePlatformPath(platformId)
                def nioArchiveContentPath = archiveContentPath.toPath()

                // Prepare build and release destinations
                def archiveDirectoryName = "${project.name}-archive"
                def buildArchivePath = new File(this.extension.packagerBaseDir, archiveDirectoryName)
                buildArchivePath.mkdirs()
                def releaseArchivePath = new File(this.extension.releaseBasePath, archiveDirectoryName)
                releaseArchivePath.mkdirs()

                def buildArchiveName = "${project.name}-${platformId}.7z"
                def buildArchive = new File(buildArchivePath, buildArchiveName)

                buildArchive.delete()

                // Archive contents
                def byte[] buffer = new byte[8*1024*1024]
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
                                while((length = is.read(buffer, 0, buffer.length)) > 0) {
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
                        .filter { p -> Files.isRegularFile(p) && p.fileName.toString().endsWith(CFG_EXTENSION)}
                        .each { p ->

                    def configName = p.fileName.toString()
                    configName = configName.substring(0, configName.length() - CFG_EXTENSION.length())
                    def releaseExecutable = new File(releaseArchivePath, "${project.name}-${configName}-${platformId}.exe")

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

/**
 * Java packager task
 * Created by masc on 22.07.15.
 */
class PackagerNativeBundleTask extends PackagerTask {
    def String group = GROUP_PACKAGER_NATIVE

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
class PackagerReleaseNativeBundleTask extends PackagerReleaseTask {
    def String group = GROUP_PACKAGER_NATIVE

    @TaskAction
    def packagerReleaseAll() {
        def releasePlatformPath = this.getReleasePlatformPath()

        def packagerPlatformDir = this.getPackagerPlatformDir()

        def packagerBundlePath = Paths.get(packagerPlatformDir.toURI())
                .resolve('bundles')
                .resolve(SystemUtils.IS_OS_MAC_OSX ? "" : project.name)
                .toFile()

        if (!packagerBundlePath.exists())
            throw new IOException("Bundle release path [${packagerBundlePath}] doesn't exist")

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

        println "Copying bundle [${packagerBundlePath}] -> [${releasePlatformPath}]"
        project.copy {
            from packagerBundlePath
            into releasePlatformPath
        }

        // TODO: workaround for bug in jdk 1.8.0_60, where jars are not picked up when building bundle
        // Jars are already missing in packager bundle dir. This worked fine with jdk 1.8.0_51
        if (SystemUtils.IS_OS_MAC_OSX) {
            def releaseBundleJarPath = this.getReleaseBundle(PlatformId.current()).jarPath
            println "Copying jars -> [${releaseBundleJarPath}]"
            project.copy {
                from this.getProjectJars()
                into releaseBundleJarPath
            }
        }

        this.copySupplementalPlatformDirs(PlatformId.current())

        println "Creating bundle manifest"
        Bundle.create(
                this.getReleasePlatformBundlePath(),
                project.name,
                PlatformId.current(),
                Bundle.Version.parse(project.version))
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
            def releaseBundle = Bundle.load(this.getReleasePlatformBundlePath(platformId))

            // Kava version check here to prevent updating jars of a native bundle built with a different version
            if (!releaseBundle.javaVersion.equals(SystemUtils.JAVA_VERSION))
                throw new IllegalStateException("Java version of bundle [${releaseBundle}] does not match currently active java version [${SystemUtils.JAVA_VERSION}]")

            println "Releasing jars and binaries for [${platformId}]"

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

            this.copySupplementalPlatformDirs(platformId)

            // Update packager configuration file (main jar name, class path, start class)
            println("Updating bundle configuration")
            def bundleConfig = releaseBundle.configuration
            bundleConfig.appMainJar = this.getMainJar().getName()
            bundleConfig.appVersion = project.version
            bundleConfig.appClassPath = this.getProjectJars().stream().map { it.getName() }.collect()
            bundleConfig.save()

            println "Creating bundle manifest"
            Bundle.create(
                    this.getReleasePlatformBundlePath(platformId),
                    project.name,
                    platformId,
                    Bundle.Version.parse(project.version))

            this.createSelfExtractingArchive(platformId)
        }
    }
}

/**
 * Release push task for tagging and pushing a version to remote release repo
 */
class PackagerReleasePushTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleasePushTask() {
        println "Pushing release ${project.name}-${project.version}"

        // Wire jsch agent proxy with session factory
        def sessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "false");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                Connector con = ConnectorFactory.getDefault().createConnector()
                if (con == null)
                    throw new IllegalStateException("No jsch agent proxy connector available")

                final JSch jsch = new JSch();
                jsch.setConfig("PreferredAuthentications", "publickey");
                IdentityRepository irepo = new RemoteIdentityRepository(con);
                jsch.setIdentityRepository(irepo);
                return jsch
            }
        }
        // Provide session factory to jgit
        SshSessionFactory.setInstance(sessionFactory)

        // Git repository
        if (!this.extension.checkRepository)
            logger.warn("WARNING: Repository checks have been disabled!")

        def git = Git.open(project.rootDir)
        try {
            def repo = git.repository// FileRepositoryBuilder.create(new File(project.rootDir, ".git"))
            println "Perfoming sanity checks against git repository [${repo.directory}]"

            // Check for uncommitted changes
            def sc = git.status()
            // TODO: ignoring submodules for now, as jgit always reports them as modified, even though everything is clean
            sc.setIgnoreSubmodules(SubmoduleWalk.IgnoreSubmoduleMode.ALL)
            def status = sc.call()
            if (this.extension.checkRepository && !status.clean) {
                throw new IllegalStateException("Repository has uncommitted changes. Cannot push release")
            }

            // Fetch tags
            println "Fetching tags from git remotes"
            def fc = git.fetch()
            fc.checkFetchedObjects = true
            fc.tagOpt = TagOpt.FETCH_TAGS
            fc.call()

            // Maintain git tag, verify if it doesn't exist and push tags in order to prevent overwriting of existing versions
            def String tagName = "${project.name}-${project.version}"

            def ltc = git.tagList()
            List<Ref> tagRefs = ltc.call()

            def RevWalk walk = new RevWalk(repo);

            // Walk revs and map to RevTag
            def RevTag tag = tagRefs.stream()
                    .map { tr -> walk.parseTag(tr.objectId) }
                    .filter { t -> t.tagName.equals(tagName) }
                    .findFirst().orElse(null)

            if (tag != null) {
                // Commit the tag points to
                def RevCommit tagCommit = tag.getObject()
                // Current branch commit
                def RevCommit currentCommit = walk.parseCommit(repo.getRef(repo.branch).objectId)

                if (this.extension.checkRepository && !currentCommit.name.equals(tagCommit.name))
                    throw new IllegalStateException("Release tag [${tagName}] already exists for [${tagCommit.name}] but current branch is on different rev [${currentCommit.name}]. " +
                            "If you intend to change the java version for this release, please manually remove (old) platform bundles not matching this java version.")
            } else {
                println "Creating tag [${tagName}]"
                def tc = git.tag()
                tc.name = tagName
                tc.call()

                try {
                    def pc = git.push()
                    pc.setPushTags()
                    pc.setPushAll()
                    println "Pushing to git remote [${pc.remote}]"
                    pc.call()
                } catch(Exception ex) {
                    def tdc = git.tagDelete()
                    tdc.tags = tagName
                    tdc.call()
                    throw ex
                }
            }
        } finally {
            git.close()
        }

        // Upload to bundle repository
        BundleRepository ar = BundleRepositoryConfiguration.INSTANCE$.stagingRepository()
        ar.upload(project.name, this.getReleasePath(), true)
    }
}

/**
 * Release pull task downloads (and overwrites) remote release with version equal or less than the current project version
 * Used to "seed" the release directory with bundle for all platforms
 */
class PackagerReleasePullTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleasePullTask() {
        def releasePath = this.getReleasePath()

        def version = Bundle.Version.parse(project.version)
        BundleRepository repository = BundleRepositoryFactory.INSTANCE$.stagingRepository()

        def remoteVersions = repository.listVersions(project.name)
                .stream()
                .filter { v -> v.compareTo(version) <= 0 }
                .sorted().collect(Collectors.toList()).reverse()

        if (remoteVersions.size() == 0)
            throw new IllegalStateException("No remote versions <= ${version}")

        repository.download(project.name, remoteVersions.get(0), releasePath, true, true)
    }
}

/**
 * Cleans the release directory for this project (by removing and recreating it)
 */
class PackagerReleaseCleanTask extends PackagerReleaseTask {
    @TaskAction
    def packagerReleaseCleanTask() {
        def releasePath = this.getReleasePath()

        releasePath.deleteDir()
        releasePath.mkdirs()
    }
}
