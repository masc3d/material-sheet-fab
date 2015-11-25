package sx.ssh

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

/**
 * Created by masc on 23.11.15.
 */
@Ignore
class SshTunnelTest {
    @Test
    fun testConnection() {
        val tunnel = SshTunnel(host = "10.211.55.7",
                sshPort = 13003,
                remotePort = 13002,
                localPort = 13050,
                sshUsername = "leoz",
                sshPassword = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8")

        tunnel.open()
        tunnel.close()
    }

    @Test
    fun testAuthenticationFailure() {
        val tunnel = SshTunnel(host = "10.211.55.7",
                sshPort = 13003,
                remotePort = 13002,
                localPort = 13050,
                sshUsername = "leoz",
                sshPassword = "meh")

        try {
            tunnel.open()
            tunnel.close()
            Assert.fail()
        } catch(e: SshTunnel.AuthenticationException) {
            // ok
        }
    }
}