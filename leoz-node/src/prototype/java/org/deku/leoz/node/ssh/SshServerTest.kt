package org.deku.leoz.node.ssh

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.forward.AcceptAllForwardingFilter
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.PrototypeTest
import java.nio.file.Files

/**
 * Created by masc on 13.11.15.
 */
@Category(PrototypeTest::class)
class SshServerTest {
    @Test
    fun testRun() {
        val lRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        lRoot.level = Level.DEBUG

        val sshd = SshServer.setUpDefaultServer()
        sshd.port = 13005

        sshd.keyPairProvider = SimpleGeneratorHostKeyProvider(Files.createTempFile("hostkey", "ser"))

        sshd.forwardingFilter = AcceptAllForwardingFilter()

        sshd.passwordAuthenticator = object : PasswordAuthenticator {
            override fun authenticate(username: String?, password: String?, session: ServerSession?): Boolean {
                return "leoz".equals(username) && "leoz".equals(password)
            }
        }

        //sshd.setShellFactory(MyCommandFactory());
        sshd.start()
        Thread.sleep(Long.MAX_VALUE)
    }

}