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
    val connector by lazy {
        ConnectorFactory.getDefault().createConnector() ?: throw IllegalStateException("No jsch agent proxy connector available")
    }

    override fun configure(host: OpenSshConfig.Host, session: Session) {
        session.setConfig("StrictHostKeyChecking", "false")
    }

    override fun getJSch(hc: OpenSshConfig.Host?, fs: FS?): JSch {
        return super.getJSch(hc, fs).also {
            // Wire jsch agent proxy with session factory
            it.identityRepository = RemoteIdentityRepository(this.connector)

            // Since jgit-4.9, the config repository will be set to OpenSshConfig, which will make USERAUTH fail since identity repository will be tried last
            // {@see UserAuthPublicKey.start}
            it.configRepository = null
        }
    }
}