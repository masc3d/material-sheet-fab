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
        rsyncClient.destination = Rsync.URI("rsync://leoz@10.211.55.7:13002/bundles/leoz-boot")
        rsyncClient.password = "2FBVQsfQqZOgpbSSipdZuatQCuaogyfYc9noFYRZO6gz3TwGRDLDiGXkRJ70yw5x"

        rsyncClient.sshTunnel = SshTunnel(host = "10.211.55.7",
                port = 13003,
                remoteTunnelPort = 13002,
                localTunnelPort = 13050,
                userName = "leoz",
                password = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8")

        var result = rsyncClient.list()
        println(result)
    }
}