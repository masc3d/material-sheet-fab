package sx.rsync

import org.junit.Test
import java.net.URI
import java.net.URL
import java.nio.file.Paths

/**
 * Created by masc on 15.08.15.
 */
class RsyncClientTest : RsyncTest() {

    @Test
    fun testClient() {
        val rsyncClient = RsyncClient(this.rsyncExecutablePath)
        rsyncClient.source = RsyncClient.URI("rsync://leoz@syntronix.de/leoz/test")
        var path = Paths.get("").toAbsolutePath().getParent().getParent().getParent().resolve("leoz-release")

        rsyncClient.destination = RsyncClient.URI(path)
        rsyncClient.password = "leoz"
        rsyncClient.compression = 9
        rsyncClient.delete = true
        rsyncClient.sync({ fr -> this.log.info(fr) }, {})
    }
}