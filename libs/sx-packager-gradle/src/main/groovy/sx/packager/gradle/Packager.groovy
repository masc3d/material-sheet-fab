package sx.packager.gradle

import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.agentproxy.ConnectorFactory
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import com.jcraft.jsch.agentproxy.connector.PageantConnector
import org.apache.commons.lang3.SystemUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectIdRef
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
import sx.packager.Bundle
import sx.packager.BundleRepository
import sx.platform.PlatformId

import java.util.stream.Collectors

/**
 * Base class for all packager tasks
 */
abstract class Task extends DefaultTask {
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
    protected Set<File> getProjectJars() {
        return project.configurations.compile.files + [project.tasks.jar.archivePath]
    }

    /**
     * Project main jar
     * @return
     */
    protected File getMainJar() {
        return project.tasks.jar.archivePath
    }

    /**
     * Main jar main class
     * @return
     */
    protected String getMainClassName() {
        return project.mainClassName
    }

    /**
     * Packager platform/arch path for current system
     * @return
     */
    protected File getPackagerPlatformDir() {
        return new File(this.extension.packagerBaseDir, PlatformId.current().toString())
    }

    /**
     * Packager platform/arch bundles path
     * @return
     */
    protected File getPackagerPlatformBundlesDir() {
        return new File(this.getPackagerPlatformDir(), 'bundles')
    }
}

/**
 * Base class for all packager release tasks
 */
abstract class ReleaseTask extends Task {
    /**
     * Build release path based on project name
     * @return
     */
    File getReleasePath() {
        def releasePath = new File(this.extension.getReleaseBasePath(), this.extension.bundleName)
        releasePath.mkdirs()
        return releasePath
    }

    /**
     * Builds a release path for current project and specific platform/arch
     * @param basePath Release base path
     * @param platformId Platform/arch
     * @return
     */
    File getReleasePlatformPath(PlatformId platformId) {
        def releasePlatformPath = new File(this.getReleasePath(), platformId.toString())
        releasePlatformPath.mkdirs()
        return releasePlatformPath
    }

    /**
     * Builds a release path for current project and platform/arch
     * @param basePath Release base path
     * @return
     */
    File getReleasePlatformPath() {
        return this.getReleasePlatformPath(PlatformId.current())
    }

    /**
     * Returns bundle for platform
     * @param platformId
     * @return
     */
    Bundle getReleaseBundle(PlatformId platformId) {
        return new Bundle(
                this.getReleasePlatformPath(platformId),
                this.extension.bundleName,
                Bundle.Version.parse(this.extension.version),
                platformId)
    }

    /**
     * Gets a release supplmental path
     * @param platformId Platform
     * @param relativePath Relative sub path
     * @return
     */
    protected getReleaseSupplementalPath(PlatformId platformId, File relativePath) {
        return new File(this.getReleaseBundle(platformId).contentPath, relativePath.toString())
    }

    /**
     * Copy supplemental dirs (both regular and platform specific)
     * @param platformId
     * @return
     */
    protected copySupplementalDirs(PlatformId platformId) {
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
}

/**
 * Release push task for tagging and pushing a version to remote release repo
 */
class ReleasePushTask extends ReleaseTask {
    @TaskAction
    packagerReleasePushTask() {
        println "Pushing release ${this.extension.bundleName}-${this.extension.version}"

        // Provide session factory to jgit
        SshSessionFactory.setInstance(new sx.jsch.ConfigSessionFactory())

        // Git repository
        if (!this.extension.checkRepository)
            logger.warn("WARNING: Repository checks have been disabled!")

        def git = Git.open(this.extension.gitRoot)
        try {
            def repo = git.repository
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
            def String tagName = "${this.extension.bundleName}/${this.extension.version}"

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
                def RevObject tagCommit = tag.getObject()
                // Current branch commit
                def currentCommitRefName = repo.branch
                // Attempt to resolve a branch name (eg. master) to ref
                def currentCommitRef = repo.findRef(repo.branch)
                if (currentCommitRef != null) {
                    currentCommitRefName = walk.parseCommit(currentCommitRef.objectId).name
                }

                if (this.extension.checkRepository && !currentCommitRefName.equals(tagCommit.name))
                    throw new IllegalStateException("Release tag [${tagName}] already exists for [${tagCommit.name}] but current branch is on different rev [${currentCommitRefName}]. " +
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
                } catch (Exception ex) {
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
        BundleRepository ar = this.extension.bundleRepository
        try {
            ar.upload(this.extension.bundleName, this.getReleasePath(), true)
        } finally {
            this.extension.sshTunnelProvider?.close()
        }
    }
}

/**
 * Release pull task downloads (and overwrites) remote release with version equal or less than the current project version
 * Used to "seed" the release directory with bundle for all platforms
 */
class ReleasePullTask extends ReleaseTask {
    @TaskAction
    packagerReleasePullTask() {
        def releasePath = this.getReleasePath()

        def version = Bundle.Version.parse(this.extension.version)
        BundleRepository repository = this.extension.bundleRepository

        try {
            def remoteVersions = repository.listVersions(this.extension.bundleName)
                    .stream()
                    .filter { v -> v.compareTo(version) <= 0 }
                    .sorted().collect(Collectors.toList()).reverse()

            if (remoteVersions.size() == 0)
                throw new IllegalStateException("No remote versions <= ${version}")

            repository.download(
                    this.extension.bundleName,
                    remoteVersions.get(0),
                    releasePath,
                    new ArrayList<File>(),
                    true,
                    true)
        } finally {
            this.extension.sshTunnelProvider?.close()
        }
    }
}

/**
 * Cleans the release directory for this project (by removing and recreating it)
 */
class ReleaseCleanTask extends ReleaseTask {
    @TaskAction
    packagerReleaseCleanTask() {
        def releasePath = this.getReleasePath()

        releasePath.deleteDir()
        releasePath.mkdirs()
    }
}
