package sx.rsync

import org.apache.commons.logging.LogFactory
import org.junit.Ignore
import org.junit.Test
import java.nio.file.Paths

/**
 * Created by masc on 01.09.15.
 */
@Ignore
class RsyncServerTest {
    private val log = LogFactory.getLog(this.javaClass)

    private val modulePath = Paths.get("").toAbsolutePath()
            .parent
            .parent
            .parent
            .resolve("leoz-release")

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