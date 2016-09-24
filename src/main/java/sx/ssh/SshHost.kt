package sx.ssh

/**
 * SSH host
 * @param hostname SSH host to connect to
 * @param port SSH port
 * @param username SSH username
 * @param password SSH password
 */
class SshHost(
        val hostname: String,
        val port: Int,
        val username: String,
        val password: String) {

    override fun toString(): String {
        return "${this.javaClass.simpleName}(hostname=${hostname}, port=${port})"
    }
}