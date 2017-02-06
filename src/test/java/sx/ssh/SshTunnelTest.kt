package sx.ssh

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

/**
 * Created by masc on 23.11.15.
 */
class SshTunnelTest {
    @Test
    fun testConnection() {
        val tunnel = SshTunnel(
                sshHost = SshHosts.testHost,
                remotePort = 13002,
                localPort = 13050)

        tunnel.open()
        tunnel.close()
    }

    @Test
    fun testAuthenticationFailure() {
        val tunnel = SshTunnel(
                sshHost = SshHosts.testHostWithInvalidCredentials,
                remotePort = 13002,
                localPort = 13050)
        try {
            tunnel.open()
            tunnel.close()
            Assert.fail()
        } catch(e: SshTunnel.AuthenticationException) {
            // ok
        }
    }
}