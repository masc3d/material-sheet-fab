package org.deku.leoz.node.ssh

import org.apache.sshd.client.SshClient
import org.apache.sshd.client.auth.UserAuth
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.SshdSocketAddress
import org.apache.sshd.common.forward.DefaultTcpipForwarder
import org.apache.sshd.common.forward.DefaultTcpipForwarderFactory
import org.apache.sshd.common.forward.TcpipForwarderFactory
import org.junit.Ignore
import org.junit.Test

/**
 * Created by masc on 13.11.15.
 */
@Ignore
class SshClientTest {
    @Test
    fun testRun() {
        val ssh = SshClient.setUpDefaultClient()

        ssh.start()

        val connectFuture = ssh.connect("leoz", "localhost", 13005)
        connectFuture.await()
        val session = connectFuture.session
        session.addPasswordIdentity("leoz")
        session.auth()

        session.startLocalPortForwarding(SshdSocketAddress("localhost", 13010), SshdSocketAddress("localhost", 13000))

        Thread.sleep(Long.MAX_VALUE)
    }
}