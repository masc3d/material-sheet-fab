package org.deku.leoz.ui

import com.sun.jna.platform.win32.Shell32Util
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleProcessInterface
import org.slf4j.LoggerFactory
import sx.io.WindowsShellLink
import java.io.File

/**
 * Created by masc on 05-Feb-16.
 */
class Setup : BundleProcessInterface() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val CSIDL_DESKTOP = 0

    private val bundle by lazy({
        Bundle.load(this.javaClass)
    })
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
}