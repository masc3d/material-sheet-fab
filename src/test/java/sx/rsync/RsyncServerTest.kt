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

        var module = RsyncServer.Configuration.Module("leoz", File("").getAbsoluteFile())

        var user = RsyncServer.Configuration.User("leoz", "testtest")
        module.permissions.put(user, RsyncServer.Configuration.Permission.READWRITE)

        config.modules.add(module)

        println("CONFIG:")
        config.save(System.out)

        println("SECRETS:")
        config.saveSecrets(System.out)
    }
}