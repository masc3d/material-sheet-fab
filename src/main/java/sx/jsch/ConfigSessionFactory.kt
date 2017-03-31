package sx.jsch

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.agentproxy.ConnectorFactory
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.util.FS

/**
 * Common JSCH config session factory which uses public key as preferred authentication and disables strict host key checking
 */
class ConfigSessionFactory : org.eclipse.jgit.transport.JschConfigSessionFactory() {
    override fun configure(hc: OpenSshConfig.Host, session: Session) {
        session.setConfig("StrictHostKeyChecking", "false")
    }

    override fun createDefaultJSch(fs: FS): JSch {
        val con = ConnectorFactory.getDefault().createConnector()

        if (con == null)
            throw IllegalStateException("No jsch agent proxy connector available")

        JSch.setConfig("PreferredAuthentications", "publickey")
        val jsch = JSch()
        val irepo = RemoteIdentityRepository(con)
        jsch.identityRepository = irepo
        return jsch
    }
}