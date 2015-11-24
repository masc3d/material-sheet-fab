package sx.rsync

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.commons.logging.LogFactory
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.ssh.SshTunnel
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by masc on 15.08.15.
 */
@Ignore
class RsyncClientTest {
    val log = LogFactory.getLog(this.javaClass)

    val rsyncSource = Rsync.URI("rsync://leoz@10.211.55.7:13002/bundles/leoz-boot/0.4-RELEASE/win64")
    val rsyncPassword = "2FBVQsfQqZOgpbSSipdZuatQCuaogyfYc9noFYRZO6gz3TwGRDLDiGXkRJ70yw5x"

    val rsyncTunneledSource = Rsync.URI(uri = this.rsyncSource.uri, sshTunnel = SshTunnel(host = "10.211.55.7",
            port = 13003,
            remoteTunnelPort = 13002,
            localTunnelPort = 13050,
            userName = "leoz",
            password = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8"))

    val localPath = Paths.get("").toAbsolutePath().parent.parent.parent.resolve("leoz-release").resolve("test")

    init {
        val lRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        lRoot.level = Level.INFO
    }

    private fun createRsyncClient(): RsyncClient {
        val rsyncClient = RsyncClient()
        rsyncClient.password = this.rsyncPassword
        rsyncClient.compression = 2
        rsyncClient.delete = true
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
        val rsyncClient = this.createRsyncClient()
        val source = this.rsyncTunneledSource
        val destination = Rsync.URI(this.localPath)

        rsyncClient.sync(source, destination, { fr -> println(fr) }, {})
    }

    @Test
    fun testThreadedTunneledSync() {
        val rsyncClient = this.createRsyncClient()

        val threads = ArrayList<Thread>()
        for (i in 0..10) {
            threads.add(thread {
                try {
                    val path = this.localPath.resolve(i.toString())
                    path.toFile().mkdirs()

                    val sshTunnel = this.rsyncTunneledSource.sshTunnel!!

                    // Use separate tunnel for each connection.
                    // Sharing the tunnel among multiple threads/connections makes ssh components freeze.
                    val source = Rsync.URI(
                            uri = this.rsyncSource.uri,
                            sshTunnel = SshTunnel(
                                    host = sshTunnel.host,
                                    port = sshTunnel.port,
                                    remoteTunnelPort = sshTunnel.remoteTunnelPort,
                                    localTunnelPort = sshTunnel.localTunnelPort + i,
                                    userName = sshTunnel.userName,
                                    password = sshTunnel.password))
                    
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
        val rsyncClient = this.createRsyncClient()

        var result = rsyncClient.list(this.rsyncTunneledSource)
        println(result)
    }
}