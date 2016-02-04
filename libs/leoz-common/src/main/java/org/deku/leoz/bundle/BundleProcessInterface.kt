package org.deku.leoz.bundle

/**
 * Represents the leoz native bundle process (commandline) interface
 * Created by masc on 04/02/16.
 */
abstract class BundleProcessInterface {
    abstract fun install();
    abstract fun uninstall();
    abstract fun start();
    abstract fun stop();

    /**
     * Parse and run bundle process interface command
     * @return Runnable code block if command was parsed successfully
     */
    fun parse(args: Array<String>): Runnable? {
        if (args.size < 1)
            return null

        val command = args[0].toLowerCase().trim()

        var r: Runnable? = null
        when (command) {
            "install" -> r = Runnable { this.install() }
            "uninstall" -> r = Runnable { this.uninstall() }
            "start" -> r = Runnable { this.start() }
            "stop" -> r = Runnable { this.stop() }
        }

        return r
    }
}