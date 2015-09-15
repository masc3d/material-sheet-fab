package sx.rsync

import org.apache.commons.logging.LogFactory
import org.junit.Test
import java.io.File
import java.nio.file.Paths

/**
 * Created by masc on 01.09.15.
 */
public class RsyncServerTest {
    private val log = LogFactory.getLog(this.javaClass)

    private val modulePath = Paths.get("").toAbsolutePath()
            .getParent()
            .getParent()
            .getParent()
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
    public fun testConfiguration() {
        var config = this.createConfiguration()

        println("CONFIG:")
        config.save(System.out)

        println("SECRETS:")
        config.saveSecrets(System.out)
    }

    @Test
    public fun testServer() {
        var config = this.createConfiguration()

        var rsyncServer = RsyncServer(modulePath.toFile(), config)
        rsyncServer.onTermination = { e ->
            if (e != null)
                log.error(e.getMessage(), e)
        }
        rsyncServer.start()
        rsyncServer.waitFor()
    }
}