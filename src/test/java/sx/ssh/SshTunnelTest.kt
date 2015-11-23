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
                port = 13003,
                remoteTunnelPort = 13002,
                localTunnelPort = 13050,
                userName = "leoz",
                password = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8")

        tunnel.request()
        tunnel.release()
    }

    @Test
    fun testAuthenticationFailure() {
        val tunnel = SshTunnel(host = "10.211.55.7",
                port = 13003,
                remoteTunnelPort = 13002,
                localTunnelPort = 13050,
                userName = "leoz",
                password = "meh")

        try {
            tunnel.request()
            tunnel.release()
            Assert.fail()
        } catch(e: SshTunnel.AuthenticationException) {
            // ok
        }
    }
}