package org.deku.leoz.node

import com.google.common.base.StandardSystemProperty
import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.slf4j.Logger
import sx.ProcessExecutor
import java.nio.file.Path
import java.nio.file.Paths
import java.util
import kotlin.platform.platformStatic

/**
 * Local Storage
 * Created by masc on 26.06.15.
 */
class Setup {
    private var log: Log = LogFactory.getLog(this.javaClass)

    companion object Singleton {
        private val instance: Setup = Setup()
        @platformStatic fun instance(): Setup {
            return this.instance;
        }
    }

    private var basePath: Path
    private var binPath: Path
    private var codeSourcePath: Path
    private var leozsvcPath: Path

    /**
     * Process exception
     */
    public class ProcessException(errorCode: Int) : Exception("Process failed with error code [${errorCode}]") {
        var errorCode = errorCode
    }

    /**
     * Bin os/arch subdiretory name
     */
    private fun binArchDirectoryName(): String {
        var prefix: String

        when {
            SystemUtils.IS_OS_WINDOWS -> prefix = "win"
            SystemUtils.IS_OS_LINUX -> prefix = "linux"
            SystemUtils.IS_OS_MAC_OSX -> prefix = "osx"
            else -> throw IllegalStateException("Unsupported platform")
        }
        when (SystemUtils.OS_ARCH) {
            "amd64" -> prefix += "64"
            "x86" -> prefix += "32"
            else -> throw IllegalStateException("Unsupported architecture")
        }
        return prefix
    }

    private constructor() {
        this.codeSourcePath = Paths.get(this.javaClass.getProtectionDomain().getCodeSource().getLocation().toURI())
        if (this.codeSourcePath.toString().endsWith(".jar")) {
            // Running from within jar. Parent directory is supposed to contain bin\ directory for service installation
            this.basePath = this.codeSourcePath.getParent().getParent()
            this.binPath = this.basePath.resolve("bin");
        } else {
            // Assume running from ide, working dir plus arch bin path
            this.basePath =  Paths.get("").toAbsolutePath()
            this.binPath = this.basePath.resolve("bin").resolve(binArchDirectoryName())
        }

        leozsvcPath = this.binPath.resolve("leoz-svc.exe")
        log.info("Setup base path [${basePath}] bin path [${binPath}]")
    }

    /**
     * Execute command
     */
    private fun execute(pb: ProcessBuilder) {
        var output = StringBuffer()
        var error = StringBuffer()

        // Execute
        var pe: ProcessExecutor = ProcessExecutor(pb, object : ProcessExecutor.StreamHandler {
            override fun onError(o: String?) {
                output.append(o + StandardSystemProperty.LINE_SEPARATOR.value())
            }

            override fun onOutput(o: String?) {
                error.append(o + StandardSystemProperty.LINE_SEPARATOR.value())
            }
        })

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
            var lines = output.splitBy(StandardSystemProperty.LINE_SEPARATOR.value())
            log.info("Lines [${lines.count()}]")
            for (line in lines) {
                var tLine = line.trim()
                if (tLine.length() > 0)
                    if (isError)
                        log.error(tLine)
                    else
                        log.info(tLine)
            }
        }
    }

    /**
     * Installs node as a system service
     */
    public fun install(serviceName: String, mainClass: Class<Any>) {
        log.info("Installing service")

        var classPath = Paths.get(mainClass.getProtectionDomain().getCodeSource().getLocation().toURI())

        var pb: ProcessBuilder = ProcessBuilder(this.leozsvcPath.toString(),
                "//IS/LeoZ",
                "--DisplayName=${serviceName}",
                "--Description=LeoZ node system service",
                "--Install=${this.leozsvcPath}",
                "--Startup=auto",
                "--LogPath=${LocalStorage.instance().logDirectory}",
                "--LogPrefix=leoz-svc",
                "--Jvm=${basePath.resolve("runtime").resolve("bin").resolve("server").resolve("jvm.dll")}",
                "--StartMode=jvm",
                "--StopMode=jvm",
                "--StartClass=${mainClass.getCanonicalName()}",
                "--StartMethod=main",
                "--StopClass=${mainClass.getCanonicalName()}",
                "--StopMethod=stop",
                "--Classpath=${classPath}")

        log.trace("Command ${java.lang.String.join(" ", pb.command())}")
        this.execute(pb)

        log.info("Installed successfully")
    }

    /**
     * Uninstalls node system service
     */
    public fun uninstall() {
        log.info("Uninstalling service")

        var pb: ProcessBuilder = ProcessBuilder(this.leozsvcPath.toString(),
                "//DS/LeoZ")
        this.execute(pb)

        log.info("Uninstalled successfully")
    }

    /**
     * Start
     */
    public fun start() {
        log.info("Starting service")

        var pb: ProcessBuilder = ProcessBuilder("net", "start", "LeoZ")
        this.execute(pb)

        log.info("Started sucessfully")
    }

    /**
     * Stop
     */
    public fun stop() {
        log.info("Stopping service")

        var pb: ProcessBuilder = ProcessBuilder("net", "stop", "LeoZ")
        this.execute(pb)

        log.info("Stopped successfully")
    }
}
