package org.deku.leoz.node.ssh

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.UserAuth
import org.apache.sshd.server.auth.UserAuthPasswordFactory
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.forward.AcceptAllForwardingFilter
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files

/**
 * Created by masc on 13.11.15.
 */
@Ignore
class SshServerTest {
    @Test
    fun testRun() {
        val lRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        lRoot.level = Level.DEBUG

        val sshd = SshServer.setUpDefaultServer()
        sshd.setPort(13005)

        sshd.setKeyPairProvider(SimpleGeneratorHostKeyProvider(Files.createTempFile("hostkey", "ser")));

        val userAuthFactories = arrayListOf<NamedFactory<UserAuth>>()
        userAuthFactories.add(UserAuthPasswordFactory.INSTANCE);
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.tcpipForwardingFilter = AcceptAllForwardingFilter()

        sshd.setPasswordAuthenticator(object : PasswordAuthenticator {
            override fun authenticate(username: String?, password: String?, session: ServerSession?): Boolean {
                return "leoz".equals(username) && "leoz".equals(password);
            }
        });

        //sshd.setShellFactory(MyCommandFactory());
        sshd.start()
        Thread.sleep(Long.MAX_VALUE)
    }

}