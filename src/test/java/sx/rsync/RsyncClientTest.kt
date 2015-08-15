package sx.rsync

import org.junit.Test

/**
 * Created by masc on 15.08.15.
 */
class RsyncClientTest : RsyncTest() {

    @Test
    fun testClient() {
        val rsyncClient = RsyncClient(this.rsyncExecutablePath)
        rsyncClient.run()
    }
}