package sx.rsync

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.ssh.SshHost
import sx.ssh.SshTunnelProvider
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by masc on 15.08.15.
 */
class RsyncClientTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    val rsyncSource = Rsync.URI("rsync://leoz@10.211.55.7:13002/bundles/leoz-boot/0.4-RELEASE/win64")
    val rsyncPassword = "2FBVQsfQqZOgpbSSipdZuatQCuaogyfYc9noFYRZO6gz3TwGRDLDiGXkRJ70yw5x"

    val sshTunnelProvider = SshTunnelProvider(13100..13200,
            SshHost(hostname = "10.211.55.7",
                    port = 13003,
                    username = "leoz",
                    password = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8"))

    val localPath = Paths.get("").toAbsolutePath().parent.parent.resolve("release").resolve("testInitNull")

    init {
    }

    private fun createRsyncClient(sshTunnelProvider: SshTunnelProvider? = null): RsyncClient {
        val rsyncClient = RsyncClient()
        rsyncClient.password = this.rsyncPassword
        rsyncClient.compression = 2
        rsyncClient.delete = true
        rsyncClient.sshTunnelProvider = sshTunnelProvider
        rsyncClient.timeout = 2
        return rsyncClient
    }

    @Test
    fun testSync() {
        val rsyncClient = this.createRsyncClient()
        val source = this.rsyncSource
        val destination = Rsync.URI(this.localPath)

        rsyncClient.sync(source, destination, { fr -> println(fr) }, {})
    }

    @Test
    fun testThreadedSync() {
        val threads = ArrayList<Thread>()
        for (i in 0..10) {
            threads.add(thread {
                try {
                    val path = this.localPath.resolve(i.toString())
                    path.toFile().mkdirs()

                    val rsyncClient = this.createRsyncClient()
                    val source = this.rsyncSource
                    val destination = Rsync.URI(path)

                    rsyncClient.sync(source, destination, { fr -> println(fr) }, {})
                } catch(e: Exception) {
                    log.error(e.message, e)
                    Assert.fail()
                }
            })
        }
        threads.forEach { t -> t.join() }
    }

    @Test
    fun testTunneledSync() {
        val rsyncClient = this.createRsyncClient(this.sshTunnelProvider)
        val source = this.rsyncSource
        val destination = Rsync.URI(this.localPath)

        rsyncClient.sync(source, destination, { fr -> println(fr) }, {})
    }

    @Test
    fun testThreadedTunneledSync() {
        val rsyncClient = this.createRsyncClient(this.sshTunnelProvider)

        val threads = ArrayList<Thread>()
        for (i in 0..10) {
            threads.add(thread {
                try {
                    val path = this.localPath.resolve(i.toString())
                    path.toFile().mkdirs()

                    val source = this.rsyncSource
                    val destination = Rsync.URI(path)
                    rsyncClient.sync(source, destination, { fr -> println(fr) }, {})
                } catch(e: Exception) {
                    log.error(e.message, e)
                    Assert.fail()
                }
            })
        }
        threads.forEach { t -> t.join() }
    }

    @Test
    fun testList() {
        val rsyncClient = this.createRsyncClient()
        val uri = this.rsyncSource

        var result = rsyncClient.list(uri)
    }

    @Test
    fun testTunneledList() {
        val rsyncClient = this.createRsyncClient(this.sshTunnelProvider)

        var result = rsyncClient.list(this.rsyncSource)
        println(result)
    }
}