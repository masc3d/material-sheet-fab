package org.deku.leoz.node

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.YamlPersistence
import sx.packager.BundleProcessInterface
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.node.config.RemotePeerConfiguration
import org.ini4j.Ini
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import sx.EmbeddedExecutable
import sx.ProcessExecutor
import sx.annotationOfType
import sx.packager.Bundle
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Local Storage
 * Created by masc on 26.06.15.
 * @param bundleName Bundle name
 * @param mainClass Main class for this process
 **/
abstract class Setup(
        val bundleName: String,
        val mainClass: Class<*>) : BundleProcessInterface() {

    protected var log = LoggerFactory.getLogger(this.javaClass)
    protected val storage: Storage by Kodein.global.lazy.instance()

    /** Setup base path */
    var basePath: Path
        protected set

    companion object {
        fun create(bundleName: String, mainClass: Class<*>): Setup {
            return when {
                SystemUtils.IS_OS_WINDOWS ->
                    WindowsSetup(bundleName = bundleName, mainClass = mainClass)

                SystemUtils.IS_OS_LINUX ->
                    LinuxSetup(bundleName = bundleName, mainClass = mainClass)

                else ->
                    NoopSetup(bundleName = bundleName, mainClass = mainClass)
            }
        }
    }

    /**
     * Executes command.+
     * @param command Command to execute
     * @param logError If error should be logged
     * @return ProcessExecutor result
     */
    protected fun execute(vararg command: String, logError: Boolean = true): ProcessExecutor.Result {
        log.trace("Command ${command.joinToString(" ")}")

        /** Log helper */
        fun logResult(result: ProcessExecutor.Result) {
            if (result.output.isNotEmpty())
                log.info(result.output)
            if (result.error.isNotEmpty()) {
                if (result.exitCode == 0)
                    log.info(result.error)
                else
                    log.error(result.error)
            }
        }

        return try {
            ProcessExecutor.run(
                    command = command.toList(),
                    trim = true,
                    omitEmptyLines = true).also {
                logResult(it)
            }
        } catch (e: ProcessExecutor.ProcessRunException) {
            if (logError)
                logResult(e.result)

            throw(e)
        }
    }

    /**
     * Prepare for productive environment
     */
    override fun prepareProduction() {
        when (this.bundleName) {
            BundleType.LEOZ_NODE.value -> {
                if (storage.applicationConfigurationFile.exists()) {
                    log.warn("Skipping generation of productive configuration file [${storage.applicationConfigurationFile}]")
                    return
                }

                log.info("Generating productive configuration file [${storage.applicationConfigurationFile}]")

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
                            toFile = storage.applicationConfigurationFile)
                } catch (e: Exception) {
                    storage.applicationConfigurationFile.delete()
                    throw(e)
                }
            }
        }
    }

    /**
     * Setup implementation which doesn't do anything (used for unsupported operating systems)
     */
    class NoopSetup(
            bundleName: String,
            mainClass: Class<*>
    ) : Setup(
            bundleName = bundleName, mainClass = mainClass
    ) {
        override fun prepareProduction() {}

        override fun install() {}

        override fun uninstall() {}

        override fun start() {}

        override fun stop() {}
    }

    /**
     * Windows setup implementation
     */
    class WindowsSetup(
            bundleName: String,
            mainClass: Class<*>
    ) : Setup(
            bundleName = bundleName, mainClass = mainClass
    ) {
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

        /**
         * Installs node as a system service
         * @param serviceName Service name
         * @param description Service description
         */
        override fun install() {
            log.info("Installing service")

            val classPath = Paths.get(mainClass.protectionDomain.codeSource.location.toURI())

            this.execute(
                    this.leozsvcExecutable.file.toString(),
                    "//IS/${this.serviceId}",
                    "--DisplayName=Leoz service (${this.serviceId})",
                    "--Description=Leoz system service (${this.serviceId})",
                    "--Install=${this.leozsvcExecutable.file.toString()}",
                    "--Startup=auto",
                    "--LogPath=${this.storage.logDirectory}",
                    "--LogPrefix=leoz-svc",
                    "--Jvm=${basePath.resolve("runtime").resolve("bin").resolve("server").resolve("jvm.dll")}",
                    "--StartMode=jvm",
                    "--StopMode=jvm",
                    "--StartClass=${mainClass.canonicalName}",
                    "--StartMethod=main",
                    "--StopClass=${mainClass.canonicalName}",
                    "--StopMethod=stop",
                    "--StopTimeout=15",
                    "--Classpath=${classPath}")

            log.info("Installed successfully")
        }

        /**
         * Uninstalls node system service
         */
        override fun uninstall() {
            if (serviceStatus() == ServiceStatus.NOT_FOUND) {
                log.info("Service already uninstalled")
                return
            }

            log.info("Uninstalling service")

            this.execute(
                    this.leozsvcExecutable.file.toString(),
                    "//DS/${serviceId}")

            log.info("Uninstalled successfully")
        }

        /**
         * Start
         */
        override fun start() {
            log.info("Starting service")

            this.execute("net", "start", serviceId)

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

            this.execute("net", "stop", serviceId)

            log.info("Stopped successfully")
        }

        /**
         * Determimes service status
         */
        private fun serviceStatus(): ServiceStatus {
            try {
                val result = ProcessExecutor.run(
                        command = listOf("sc", "query", serviceId),
                        trim = true,
                        omitEmptyLines = true)

                val re = Regex("STATE.*:.*([0-9]+)[\\s]+([A-Z]+).*")
                val mr = re.find(result.output)
                if (mr != null) {
                    val state = mr.groups[1]!!.value.toInt()
                    when (state) {
                        1 -> return ServiceStatus.STOPPED
                        else -> return ServiceStatus.NOT_STOPPED
                    }
                }
            } catch (e: ProcessExecutor.ProcessException) {
                when (e.errorCode) {
                    1060 -> return ServiceStatus.NOT_FOUND
                    else -> throw e
                }
            }

            return ServiceStatus.NOT_STOPPED
        }
    }

    /**
     * Linux setup implementation
     */
    class LinuxSetup(
            bundleName: String,
            mainClass: Class<*>)
        :
            Setup(
                    bundleName = bundleName, mainClass = mainClass
            ) {

        private val serviceName by lazy {
            this.bundleName
        }

        private val systemdServiceFile by lazy {
            File("/etc/systemd/system/${serviceName}.service")
        }

        private val systemdUserServiceFile by lazy {
            storage.userHomeDirectory
                    .resolve(".config")
                    .resolve("systemd")
                    .resolve("user")
                    .resolve("${serviceName}.service")
        }

        /** Execute systemd command */
        private fun executeSystemd(vararg command: String, userMode: Boolean = false, logError: Boolean = true) {
            this.execute(logError = logError, command = *arrayOf(
                    "systemctl"
            )
                    .let {
                        if (userMode) it.plus("--user") else it
                    }
                    .plus(command))
        }

        /** systemd reload */
        private fun systemdReload() {
            this.executeSystemd("daemon-reload")
        }

        override fun install() {
            // Write systemd config
            Ini().also { ini ->
                ini.config.isStrictOperator = true

                ini.add("Unit").also {
                    it["Description"] = "${serviceName} system service"
                }

                ini.add("Service").also {
                    it["Type"] = "simple"
                    it["ExecStart"] = "${Bundle.load(this.javaClass).executable}"
                    it["KillMode"] = "process"
                    it["Restart"] = "on-abort"
                }

                ini.add("Install").also {
                    it["WantedBy"] = "default.target"
                }
            }
                    .store(this.systemdServiceFile)

            this.systemdReload()

            this.executeSystemd("enable", serviceName)
        }

        override fun uninstall() {
            try {
                this.executeSystemd(logError = false, command = *arrayOf("disable", serviceName))
            } catch (e: ProcessExecutor.ProcessRunException) {
                log.warn("Could not disable [${serviceName}], ${e.result.error}")
            }

            this.systemdServiceFile.delete()

            this.systemdReload()
        }

        override fun start() {
            this.executeSystemd("start", serviceName)
        }

        override fun stop() {
            this.executeSystemd("stop", serviceName)
        }
    }

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
            this.basePath = Paths.get("").toAbsolutePath()
        }

        log.info("Setup base path [${basePath}]")
    }
}

