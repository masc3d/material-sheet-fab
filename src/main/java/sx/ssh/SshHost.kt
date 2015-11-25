package sx.ssh

/**
 * SSH host
 * @param hostname SSH host to connect to
 * @param sshPort SSH port
 * @param sshUsername SSH username
 * @param sshPassword SSH password
 */
data class SshHost(
        val hostname: String,
        val sshPort: Int,
        val sshUsername: String,
        val sshPassword: String) {
}