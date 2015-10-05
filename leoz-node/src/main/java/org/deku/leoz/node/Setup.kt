package org.deku.leoz.node

import com.google.common.base.StandardSystemProperty
import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import sx.platform.PlatformId
import org.slf4j.Logger
import sx.EmbeddedExecutable
import sx.ProcessExecutor
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.text.Regex

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
class Setup {
    private var log: Log = LogFactory.getLog(this.javaClass)

    companion object Singleton {
        private val instance: Setup = Setup()
        @JvmStatic fun instance(): Setup {
            return this.instance;
        }
    }

    private var basePath: Path
    private var binPath: Path
    private var codeSourcePath: Path
    private val leozsvcExecutable: EmbeddedExecutable

    private constructor() {
        this.codeSourcePath = Paths.get(this.javaClass.protectionDomain.codeSource.location.toURI())
        if (this.codeSourcePath.toString().endsWith(".jar")) {
            // Running from within jar. Parent directory is supposed to contain bin\ directory for service installation
            this.basePath = this.codeSourcePath.parent.parent
            this.binPath = this.basePath.resolve("bin");
        } else {
            // Assume running from ide, working dir plus arch bin path
            this.basePath =  Paths.get("").toAbsolutePath()
            this.binPath = this.basePath.resolve("bin").resolve(PlatformId.current().toString())
        }

        leozsvcExecutable = EmbeddedExecutable("leoz-svc")

        log.info("Setup base path [${basePath}] bin path [${binPath}]")
    }

    /**
     * Execute command
     */
    private fun execute(pb: ProcessBuilder) {
        var output = StringBuffer()
        var error = StringBuffer()

        // Execute
        var pe: ProcessExecutor = ProcessExecutor(pb,
                outputHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = output),
                errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))

        try {
            pe.start()
            pe.waitFor();
        }
        finally {
            // Evaluate/log output
            if (output.length() > 0)
                this.logProcessOutput(output.toString())
            if (error.length() > 0)
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
     * Installs node as a system service
     */
    fun install(serviceName: String, mainClass: Class<Any>) {
        log.info("Installing service")

        var classPath = Paths.get(mainClass.protectionDomain.codeSource.location.toURI())

        var pb: ProcessBuilder = ProcessBuilder(this.leozsvcExecutable.file.toString(),
                "//IS/LeoZ",
                "--DisplayName=${serviceName}",
                "--Description=LeoZ node system service",
                "--Install=${this.leozsvcExecutable.file.toString()}",
                "--Startup=auto",
                "--LogPath=${LocalStorage.instance.logDirectory}",
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

    private enum class ServiceStatus {
        STOPPED,
        NOT_STOPPED,
        NOT_FOUND
    }

    /**
     * Determimes service status
     */
    private fun serviceStatus(): ServiceStatus {
        val pb: ProcessBuilder = ProcessBuilder("sc", "query", "LeoZ")

        var output = StringBuffer()
        var error = StringBuffer()

        try {
            // Execute
            var pe: ProcessExecutor = ProcessExecutor(pb,
                    outputHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = output),
                    errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))

            pe.start()
            pe.waitFor()

            val re = Regex("STATE.*:.*([0-9]+)[\\s]+([A-Z]+).*")
            val mr = re.match(output.toString())
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

    /**
     * Uninstalls node system service
     */
    fun uninstall() {
        if (serviceStatus() == ServiceStatus.NOT_FOUND) {
            log.info("Service not found. That's ok")
            return
        }

        log.info("Uninstalling service")

        var pb: ProcessBuilder = ProcessBuilder(this.leozsvcExecutable.file.toString(),
                "//DS/LeoZ")

        this.execute(pb)

        log.info("Uninstalled successfully")
    }

    /**
     * Start
     */
    fun start() {
        log.info("Starting service")

        var pb: ProcessBuilder = ProcessBuilder("net", "start", "LeoZ")
        this.execute(pb)

        log.info("Started sucessfully")
    }

    /**
     * Stop
     */
    fun stop() {
        if (serviceStatus() != ServiceStatus.NOT_STOPPED) {
            log.info("Service does not need to be stopped")
            return
        }

        log.info("Stopping service")

        var pb: ProcessBuilder = ProcessBuilder("net", "stop", "LeoZ")
        this.execute(pb)

        log.info("Stopped successfully")
    }
}
