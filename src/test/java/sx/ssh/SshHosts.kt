package sx.ssh

/**
 * Created by masc on 19-Feb-16.
 */
object SshHosts {
    val testHost = SshHost(
            hostname = "10.211.55.7",
            port = 13003,
            username = "leoz",
            password = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8")

    val testHostWithInvalidCredentials = SshHost(
            hostname = "10.211.55.7",
            port = 13003,
            username = "leoz",
            password = "meh")
}