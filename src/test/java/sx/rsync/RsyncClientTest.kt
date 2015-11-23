package sx.rsync

import org.junit.Ignore
import org.junit.Test
import sx.ssh.SshTunnel
import java.nio.file.Paths

/**
 * Created by masc on 15.08.15.
 */
@Ignore
class RsyncClientTest {

    @Test
    fun testSync() {
        val rsyncClient = RsyncClient()
        rsyncClient.source = Rsync.URI("rsync://leoz@syntronix.de/leoz/test")
        var path = Paths.get("").toAbsolutePath().parent.parent.parent.resolve("leoz-release").resolve("test")

        rsyncClient.destination = Rsync.URI(path)
        rsyncClient.password = "leoz"
        rsyncClient.compression = 9
        rsyncClient.delete = true
        rsyncClient.sync({ fr -> println(fr) }, {})
    }

    @Test
    fun testList() {
        val rsyncClient = RsyncClient()
        rsyncClient.destination = Rsync.URI("rsync://leoz@syntronix.de/leoz/leoz-ui")

        rsyncClient.password = "leoz"
        var result = rsyncClient.list()
    }

    @Test
    fun testTunneledList() {
        val rsyncClient = RsyncClient()
        rsyncClient.destination = Rsync.URI("rsync://leoz@syntronix.de/leoz/leoz-ui")
        rsyncClient.sshTunnel = SshTunnel(host = "syntronix.de",
                port = 22,
                remoteTunnelPort = 873,
                localTunnelPort = 13000,
                userName = "leoz",
                password = "leoz")

        rsyncClient.password = "leoz"
        var result = rsyncClient.list()
    }
}