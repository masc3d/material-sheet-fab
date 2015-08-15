package sx.rsync

import org.junit.Test
import java.net.URI
import java.net.URL

/**
 * Created by masc on 15.08.15.
 */
class RsyncClientTest : RsyncTest() {

    @Test
    fun testClient() {
        val rsyncClient = RsyncClient(this.rsyncExecutablePath)
        rsyncClient.source = URI("rsync://leoz@syntronix.de/leoz/test")
        rsyncClient.destination = URI(".")
        rsyncClient.password = "leoz"
        rsyncClient.compression = 9
        rsyncClient.run()
    }
}