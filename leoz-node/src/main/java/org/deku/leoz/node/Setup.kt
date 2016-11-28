package org.deku.leoz.node

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.base.Strings
import org.deku.leoz.YamlPersistence
import org.deku.leoz.bundle.BundleProcessInterface
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.node.config.RemotePeerConfiguration
import org.deku.leoz.node.config.StorageConfiguration
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import sx.EmbeddedExecutable
import sx.ProcessExecutor
import sx.annotationOfType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Local Storage
 * Created by masc on 26.06.15.
 * @param serviceId Short service id, used for leoz-svc identifier: //IS/<serviceId>
 **/
class Setup(
        val bundleName: String,
        val mainClass: Class<*>) : BundleProcessInterface() {

    private var log = LoggerFactory.getLogger(this.javaClass)

    private var basePath: Path

    private val leozsvcExecutable: EmbeddedExecutable by lazy {
        EmbeddedExecutable("leoz-svc")
    }

    /**
     * Short service id, used for leoz-svc identifier: //IS/<serviceId>
     */
    private val serviceId: String
        get() = this.bundleName

    /**
     * Service status
     */
    private enum class ServiceStatus {
        STOPPED,
        NOT_STOPPED,
        NOT_FOUND
    }

    val storageConfiguration: StorageConfiguration by Kodein.global.lazy.instance()

    /**
     * c'tor
     */
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
                "--LogPath=${this.storageConfiguration.logDirectory}",
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
     * Prepare for productive environment
     */
    override fun prepareProduction() {
        when (this.bundleName) {
            BundleType.LEOZ_NODE.value -> {
                if (storageConfiguration.applicationConfigurationFile.exists()) {
                    log.warn("Skipping generation of productive configuration file [${storageConfiguration.applicationConfigurationFile}]")
                    return
                }

                log.info("Generating productive configuration file [${storageConfiguration.applicationConfigurationFile}]")

                val remotePeerConfig = RemotePeerConfiguration()
                remotePeerConfig.host = "leoz.derkurier.de"

                val configurationProperty = remotePeerConfig.javaClass.annotationOfType(ConfigurationProperties::class.java)
                if (configurationProperty.prefix.contains("."))
                    throw UnsupportedOperationException("Nested yaml path not supported for configuration file generation (yet)")

                val configContent = mapOf<String, Any>(Pair(configurationProperty.prefix, remotePeerConfig))

                try {
                    YamlPersistence.save(
                            obj = configContent,
                            skipNulls = true,
                            skipTags = true,
                            toFile = storageConfiguration.applicationConfigurationFile)
                } catch(e: Exception) {
                    storageConfiguration.applicationConfigurationFile.delete()
                    throw(e)
                }
            }
        }
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

        val pb: ProcessBuilder = ProcessBuilder(this.leozsvcExecutable.file.toString(),
                "//DS/${serviceId}")

        this.execute(pb)

        log.info("Uninstalled successfully")
    }

    /**
     * Start
     */
    override fun start() {
        log.info("Starting service")

        val pb: ProcessBuilder = ProcessBuilder("net", "start", serviceId)
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
            if (output.isNotEmpty())
                this.logProcessOutput(output.toString())
            if (error.isNotEmpty())
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
