package sx.ssh

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Created by masc on 19-Feb-16.
 */
class SshTunnelProviderTest {
    @Before
    fun setup() {
    }

    @Test
    fun testProvider() {
        val provider = SshTunnelProvider(
                IntRange(13000, 13250),
                SshHosts.testHost)

        val tunnelResource = provider.request(SshHosts.testHost.hostname, 13002)
//        provider.close(tunnelResource!!)
    }
}
