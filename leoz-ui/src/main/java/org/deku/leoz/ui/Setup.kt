package org.deku.leoz.ui

import com.sun.jna.platform.win32.Shell32Util
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.Storage
import org.deku.leoz.bundle.*
import org.deku.leoz.ui.config.StorageConfiguration
import org.slf4j.LoggerFactory
import sx.io.ProcessLockFile
import sx.io.WindowsShellLink
import sx.packager.Bundle
import sx.packager.BundleProcessInterface
import java.io.File

/**
 * Bundle process interface
 * Created by masc on 05-Feb-16.
 */
class Setup : BundleProcessInterface() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val CSIDL_DESKTOP = 0

    private val bundle by lazy {
        Bundle.load(this.javaClass)
    }

    private val desktopLinkFile by lazy {
        when {
            SystemUtils.IS_OS_WINDOWS -> {
                val desktopFolderPath = Shell32Util.getSpecialFolderPath(CSIDL_DESKTOP, false)
                File(desktopFolderPath).resolve("${this.bundle.name!!.capitalize()}.lnk")
            }
            else -> {
                null
            }
        }
    }

    override fun install() {
        when {
            SystemUtils.IS_OS_WINDOWS -> {
                val desktopLinkFile = this.desktopLinkFile
                if (desktopLinkFile != null) {
                    val shellLink = WindowsShellLink(desktopLinkFile.absolutePath)
                    shellLink.target = bundle.path!!.resolve("${bundle.name!!}.exe").absolutePath
                    shellLink.save()
                }
            }
        }
    }

    override fun uninstall() {
        val desktopLinkFile = this.desktopLinkFile
        if (desktopLinkFile != null) {
            if (desktopLinkFile.exists())
                desktopLinkFile.delete()
        }
    }

    override fun start() {
        val storageConfiguration: Storage = StorageConfiguration.createStorage()

        val bundle = Bundle.load(File(storageConfiguration.bundleInstallationDirectory, BundleType.LEOZ_UI.value))
        bundle.execute(wait = false)
    }

    override fun stop() {
        val storage: Storage =  StorageConfiguration.createStorage()

        val processLockFile = ProcessLockFile(
                lockFile = storage.bundleLockFile,
                pidFile = storage.bundlePidFile)

        if (!processLockFile.isOwner) {
            val pid = processLockFile.pid

            // Signal kill and wait for lock to become available
            sx.Process.kill(pid)
            processLockFile.waitForLock(
                    writeCurrentProcessPid = true
            )
        }
    }
}