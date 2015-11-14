package org.deku.leoz.node.ssh

import org.apache.sshd.client.SshClient
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

        ssh.tcpipForwarderFactory = TcpipForwarderFactory { service ->
            val forwarder = DefaultTcpipForwarder(service)
            // Configure some forwarding
            forwarder
        }
    }
}