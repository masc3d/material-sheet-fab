package org.deku.leoz

import com.sun.jna.platform.win32.Shell32Util
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
    private val desktopLinkFile  by lazy({
        val desktopFolderPath = Shell32Util.getSpecialFolderPath(CSIDL_DESKTOP, false)
        File(desktopFolderPath).resolve("${this.bundle.name!!.capitalize()}.lnk")
    })

    override fun install() {
        val shellLink = WindowsShellLink(this.desktopLinkFile.absolutePath)
        shellLink.target = bundle.path!!.resolve("${bundle.name!!}.exe").absolutePath
        shellLink.save()
    }

    override fun uninstall() {
        if (desktopLinkFile.exists())
            desktopLinkFile.delete()
    }

    override fun start() {
    }

    override fun stop() {
    }
}