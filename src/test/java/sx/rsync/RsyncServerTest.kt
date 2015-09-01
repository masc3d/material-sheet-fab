package sx.rsync

import org.junit.Test
import java.io.File

/**
 * Created by masc on 01.09.15.
 */
public class RsyncServerTest {
    @Test
    public fun testSaveConfiguration() {
        var config = RsyncServer.Configuration()
        config.useChroot = true

        // Users
        var user = Rsync.User("leoz", "testtest")


        // modules
        var module = Rsync.Module("leoz", File("").getAbsoluteFile())
        module.permissions.put(user, Rsync.Permission.READWRITE)
        config.modules.add(module)

        println("CONFIG:")
        config.save(System.out)

        println("SECRETS:")
        config.saveSecrets(System.out)
    }
}