package sx.rsync

import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths

/**
 * Created by masc on 01.09.15.
 */
class RsyncServerTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val modulePath = Paths.get("").toAbsolutePath()
            .parent
            .parent
            .resolve("release")

    private fun createConfiguration(): RsyncServer.Configuration {
        var config = RsyncServer.Configuration()
        config.port = 27000
        config.logFile = modulePath.resolve("resyncd.log").toFile()

        // Users
        var user = Rsync.User("leoz", "testtest")

        // modules
        var module = Rsync.Module("leoz", modulePath.toFile())
        module.permissions.put(user, Rsync.Permission.READWRITE)
        config.modules.add(module)

        return config
    }

    @Test
    fun testConfiguration() {
        var config = this.createConfiguration()

        println("CONFIG:")
        config.save(System.out)

        println("SECRETS:")
        config.saveSecrets(System.out)
    }

    @Test
    fun testServer() {
        var config = this.createConfiguration()

        var rsyncServer = RsyncServer(modulePath.toFile(), config)
        rsyncServer.onTermination = { e ->
            if (e != null)
                log.error(e.message, e)
        }
        rsyncServer.start()
        rsyncServer.waitFor()
    }
}