package org.deku.leo2.node

import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import sx.io.ProcessStreamReader
import java.nio.file.Path
import java.nio.file.Paths
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

        leozsvcPath = this.binPath.resolve("leozsvc.exe")
        log.info("Setup base path [${basePath}] bin path [${binPath}]")
    }

    /**
     * Execute command
     */
    private fun execute(pb: ProcessBuilder) {
        // Execute
        var p: Process = pb.start();
        var pr: ProcessStreamReader = ProcessStreamReader(p)
        var errorCode = p.waitFor();

        // Evaluate/log output
        var output = pr.getOutput()
        var error = pr.getError()
        if (!Strings.isNullOrEmpty(output))
            log.info(output)
        if (!Strings.isNullOrEmpty(error))
            log.error(error)

        // Evaluate error/throw
        if (errorCode != 0) {
            throw ProcessException(errorCode)
        }
    }

    /**
     * Installs node as a system service
     */
    public fun install(serviceName: String, mainClass: Class<Any>) {
        // Command example
        // C:\Users\n3\Projects\leo2-release\leo2-node-win64\bin\leozsvc.exe
        // IS/LeoZ
        // --DisplayName="LeoZ Service"
        // --Install=C:\Users\n3\Projects\leo2-release\leo2-node-win64\bin\leozsvc.exe
        // --Startup=auto
        // --LogPath=C:\ProgramData\LeoZ\log
        // --LogPrefix=leozsvc
        // --Jvm="C:\Users\n3\Projects\leo2-release\leo2-node-win64\runtime\bin\server\jvm.dll"
        // --StartMode=jvm
        // --StopMode=jvm
        // --StartClass=org.deku.leo2.node.Main
        // --StartMethod=main
        // --StopClass=org.deku.leo2.node.Main
        // --StopMethod=stop
        // --Classpath=C:\Users\n3\Projects\leo2-release\leo2-node-win64\app\leo2-node-0.1.jar

        log.info("Installing service")

        var classPath = Paths.get(mainClass.getProtectionDomain().getCodeSource().getLocation().toURI())

        var pb: ProcessBuilder = ProcessBuilder(this.leozsvcPath.toString(),
                "//IS/LeoZ",
                "--DisplayName=${serviceName}",
                "--Description=LeoZ node system service",
                "--Install=${this.leozsvcPath}",
                "--Startup=auto",
                "--LogPath=${LocalStorage.instance().logDirectory}",
                "--LogPrefix=leozsvc",
                "--Jvm=${basePath.resolve("runtime").resolve("bin").resolve("server").resolve("jvm.dll")}",
                "--StartMode=jvm",
                "--StopMode=jvm",
                "--StartClass=${mainClass.getCanonicalName()}",
                "--StartMethod=main",
                "--StopClass=${mainClass.getCanonicalName()}",
                "--StopMethod=stop",
                "--Classpath=${classPath}")
        // log.info(java.lang.String.join(" ", pb.command()))
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
