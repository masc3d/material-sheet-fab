package org.apache.sshd.server.forward

import org.apache.sshd.common.channel.Channel
import org.apache.sshd.common.future.CloseFuture

/**
 * Hotfixed for tunneled connections, closing SSH session when the tunneled connection breaks.
 * Fixes rsync client stalling over SSH tunnel in case of specific errors, eg. directory doesn't exist
 * Created by masc on 02-Feb-16.
 */
class DirectTcpipFactoryFixed : DirectTcpipFactory() {
    override fun create(): Channel? {
        return object : TcpipServerChannel(this.type) {
            override fun close(immediately: Boolean): CloseFuture? {
                val closeFuture = super.close(immediately)
                if (immediately) {
                    closeFuture.addListener({
                        // Trigger graceful close of SSH session when tunnel connection closes
                        this.session.close(false)
                    })
                }
                return closeFuture
            }
        }
    }
}