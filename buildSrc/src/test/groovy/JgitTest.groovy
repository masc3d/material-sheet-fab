import com.jcraft.jsch.IdentityRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.agentproxy.ConnectorFactory
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.TagOpt
import org.eclipse.jgit.util.FS
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Created by n3 on 13-Sep-15.
 */
@Ignore
public class JgitTest {
    @Before
    public void setup() {
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
    }

    @Test
    public void testFetch() {
        def git = Git.open(new File("C:\\Users\\n3\\Projects\\leoz"))
        def repo = git.repository

        // Fetch tags
        println "Fetching tags from git remotes"
        def fc = git.fetch()
        fc.checkFetchedObjects = true
        fc.tagOpt = TagOpt.FETCH_TAGS
        fc.call()
    }
}
