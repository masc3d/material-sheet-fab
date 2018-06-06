import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.agentproxy.ConnectorFactory
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.lib.ObjectIdRef
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.submodule.SubmoduleWalk
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.TagOpt
import org.eclipse.jgit.util.FS
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.jsch.ConfigSessionFactory
import sx.junit.StandardTest

import java.io.File

/**
 * Created by masc on 13-Sep-15.
 */
@Category(StandardTest::class)
class JgitTest {

    val vcsPath by lazy {
        val path = File("").absoluteFile.parentFile.parentFile
        println("Repository path [${path}]")
        path
    }

    val git by lazy {
        Git.open(this.vcsPath)
    }

    @Before
    fun setup() {
        // Provide session factory to jgit
        SshSessionFactory.setInstance(ConfigSessionFactory())
    }

    @Test
    fun testFetch() {
        val repo = this.git.repository

        // Fetch tags
        println("Fetching tags from git remotes")
        val fc = git.fetch()
        fc.isCheckFetchedObjects = true
        fc.setTagOpt(TagOpt.FETCH_TAGS)
        fc.call()
    }

    @Test
    fun testIsClean() {
        val repo = this.git.repository

        // Check for uncommitted changes
        val sc = git.status()
        // TODO: ignoring submodules for now, as jgit always reports them as modified, even though everything is clean
        sc.setIgnoreSubmodules(SubmoduleWalk.IgnoreSubmoduleMode.ALL)
        val status = sc.call()
        if (!status.isClean) {
            throw IllegalStateException("Repository has uncommitted changes. Cannot push release")
        }
    }

    @Test
    fun testFindTag() {
        val ltc = git.tagList()
        val tagRefs = ltc.call()

        val walk = RevWalk(git.repository)

        // Check if find works for all tag refs
        tagRefs.forEach {
            val tagToFind = walk.parseTag(it.objectId)
            val tagNameToFind = tagToFind.tagName

            println("${tagNameToFind}")
            // Walk revs and map to RevTag
            val tag = tagRefs.stream()
                    .map { tr -> walk.parseTag(tr.objectId) }
                    .filter { t -> t.tagName == tagNameToFind }
                    .findFirst().orElse(null)

            Assert.assertEquals(tagToFind, tag)
        }
    }
}
