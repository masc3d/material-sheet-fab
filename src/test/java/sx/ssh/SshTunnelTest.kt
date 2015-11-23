package sx.ssh

import org.junit.Ignore
import org.junit.Test

/**
 * Created by masc on 23.11.15.
 */
@Ignore
class SshTunnelTest {
    @Test
    fun testRequestRelease() {
        val tunnel = SshTunnel(host = "10.211.55.7",
                port = 13005,
                remoteTunnelPort = 13003,
                localTunnelPort = 13050,
                userName = "leoz",
                password = "leoz")

        tunnel.request()
        tunnel.release()
    }
}