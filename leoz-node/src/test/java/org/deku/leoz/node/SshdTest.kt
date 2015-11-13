package org.deku.leoz.node

import org.apache.sshd.common.NamedFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.UserAuth
import org.apache.sshd.server.auth.UserAuthPassword
import org.apache.sshd.server.auth.UserAuthPasswordFactory
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.util.*

/**
 * Created by masc on 13.11.15.
 */
@Ignore
class SshdTest {
    @Test
    fun test() {
        val sshd = SshServer.setUpDefaultServer()
        sshd.setPort(13005)
        sshd.setKeyPairProvider(SimpleGeneratorHostKeyProvider(File("hostkey.ser")));

        val userAuthFactories = arrayListOf<NamedFactory<UserAuth>>()
        userAuthFactories.add(UserAuthPasswordFactory.INSTANCE);
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setPasswordAuthenticator(object : PasswordAuthenticator {
            override fun authenticate(username: String?, password: String?, session: ServerSession?): Boolean {
                return "tomek".equals(username) && "123".equals(password);
            }
        });

        //sshd.setShellFactory(MyCommandFactory());
        sshd.start()
        Thread.sleep(Long.MAX_VALUE)
    }
}