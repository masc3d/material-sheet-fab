package org.deku.leoz.node

import com.google.common.base.Strings
import org.deku.leoz.bundle.BundleProcessInterface
import org.deku.leoz.node.config.StorageConfiguration
import org.slf4j.LoggerFactory
import sx.EmbeddedExecutable
import sx.ProcessExecutor
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Local Storage
 * Created by masc on 26.06.15.
 * @param serviceId Short service id, used for leoz-svc identifier: //IS/<serviceId>
 **/
class Setup(
        val serviceId: String,
        val mainClass: Class<*>) : BundleProcessInterface() {

    private var log = LoggerFactory.getLogger(this.javaClass)

    private var basePath: Path

    private val leozsvcExecutable: EmbeddedExecutable by lazy({
        EmbeddedExecutable("leoz-svc")
    })

    private enum class ServiceStatus {
        STOPPED,
        NOT_STOPPED,
        NOT_FOUND
    }

    init {
        val codeSourcePath = Paths.get(this.javaClass.protectionDomain.codeSource.location.toURI())
        if (codeSourcePath.toString().endsWith(".jar")) {
            // Running from within jar. Parent directory is supposed to contain bin\ directory for service installation
            this.basePath = codeSourcePath.parent.parent
        } else {
            // Assume running from ide, working dir plus arch bin path
            this.basePath =  Paths.get("").toAbsolutePath()
        }

        log.trace("Setup base path [${basePath}]")
    }

    /**
     * Installs node as a system service
     * @param serviceName Service name
     * @param description Service description
     */
    override fun install() {
        log.info("Installing service")

        val classPath = Paths.get(mainClass.protectionDomain.codeSource.location.toURI())

        val pb: ProcessBuilder = ProcessBuilder(this.leozsvcExecutable.file.toString(),
                "//IS/${this.serviceId}",
                "--DisplayName=Leoz service (${this.serviceId})",
                "--Description=Leoz system service (${this.serviceId})",
                "--Install=${this.leozsvcExecutable.file.toString()}",
                "--Startup=auto",
                "--LogPath=${StorageConfiguration.instance.logDirectory}",
                "--LogPrefix=leoz-svc",
                "--Jvm=${basePath.resolve("runtime").resolve("bin").resolve("server").resolve("jvm.dll")}",
                "--StartMode=jvm",
                "--StopMode=jvm",
                "--StartClass=${mainClass.canonicalName}",
                "--StartMethod=main",
                "--StopClass=${mainClass.canonicalName}",
                "--StopMethod=stop",
                "--Classpath=${classPath}")

        log.trace("Command ${java.lang.String.join(" ", pb.command())}")
        this.execute(pb)

        log.info("Installed successfully")
    }


    /**
     * Uninstalls node system service
     */
    override fun uninstall() {
        if (serviceStatus() == ServiceStatus.NOT_FOUND) {
            log.info("Service not found. That's ok")
            return
        }

        log.info("Uninstalling service")

        var pb: ProcessBuilder = ProcessBuilder(this.leozsvcExecutable.file.toString(),
                "//DS/${serviceId}")

        this.execute(pb)

        log.info("Uninstalled successfully")
    }

    /**
     * Start
     */
    override fun start() {
        log.info("Starting service")

        var pb: ProcessBuilder = ProcessBuilder("net", "start", serviceId)
        this.execute(pb)

        log.info("Started sucessfully")
    }

    /**
     * Stop
     */
    override fun stop() {
        if (serviceStatus() != ServiceStatus.NOT_STOPPED) {
            log.info("Service does not need to be stopped")
            return
        }

        log.info("Stopping service")

        val pb: ProcessBuilder = ProcessBuilder("net", "stop", serviceId)
        this.execute(pb)

        log.info("Stopped successfully")
    }

    /**
     * Execute command
     */
    private fun execute(pb: ProcessBuilder) {
        val output = StringBuffer()
        val error = StringBuffer()

        // Execute
        val pe: ProcessExecutor = ProcessExecutor(pb,
                outputHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = output),
                errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))

        try {
            pe.start()
            pe.waitFor();
        }
        finally {
            // Evaluate/log output
            if (output.length > 0)
                this.logProcessOutput(output.toString())
            if (error.length > 0)
                this.logProcessOutput(error.toString(), isError = true)
        }
    }

    /**
     * Logs process output line by line, skipping blank lines
     */
    private fun logProcessOutput(output: String, isError: Boolean = false) {
        if (!Strings.isNullOrEmpty(output)) {
            if (isError)
                log.error(output)
            else
                log.info(output)
        }
    }

    /**
     * Determimes service status
     */
    private fun serviceStatus(): ServiceStatus {
        val pb: ProcessBuilder = ProcessBuilder("sc", "query", serviceId)

        val output = StringBuffer()
        val error = StringBuffer()

        try {
            // Execute
            val pe: ProcessExecutor = ProcessExecutor(pb,
                    outputHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = output),
                    errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))

            pe.start()
            pe.waitFor()

            val re = Regex("STATE.*:.*([0-9]+)[\\s]+([A-Z]+).*")
            val mr = re.find(output.toString())
            if (mr != null) {
                val state = mr.groups[1]!!.value.toInt()
                when (state) {
                    1 -> return ServiceStatus.STOPPED
                    else -> return ServiceStatus.NOT_STOPPED
                }
            }
        } catch(e: ProcessExecutor.ProcessException) {
            when (e.errorCode) {
                1060 -> return ServiceStatus.NOT_FOUND
                else -> throw e
            }
        }

        return ServiceStatus.NOT_STOPPED
    }
}
