package sx.ssh

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Created by masc on 19-Feb-16.
 */
@Ignore
class SshTunnelProviderTest {
    @Before
    fun setup() {
        val lRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        lRoot.level = Level.INFO

    }

    @Test
    fun testProvider() {
        val provider = SshTunnelProvider(
                IntRange(13000, 13250),
                SshHosts.testHost)

        val tunnelResource = provider.request(SshHosts.testHost.hostname, 13002)
        provider.close(tunnelResource!!)
    }
}
