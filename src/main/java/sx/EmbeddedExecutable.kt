package sx

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.platform.PlatformId
import sx.rsync.Rsync
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.AclEntry
import java.nio.file.attribute.AclEntryPermission
import java.nio.file.attribute.AclFileAttributeView
import java.util.*

/**
 * Support class for embedded executables/processes.
 * Supports automatic search for executable in parent paths, setting executable file permissions.
 * Created by masc on 21-Sep-15.
 */
class EmbeddedExecutable(
        /** Rsync executable base filename */
        public var baseFilename: String) {

    val log = LogFactory.getLog(Rsync::class.java)

    /** Rsync executable file name */
    val filename: String by lazy {
        this.baseFilename + if (SystemUtils.IS_OS_WINDOWS) ".exe" else ""
    }

    /**
     * Find rsync executabe
     */
    private fun findExecutable(): File? {
        // Search for executable in current and parent paths
        var binPlatformRelPath = Paths.get("bin")
                .resolve(PlatformId.current().toString())
                .resolve(filename)

        var binRelPath = Paths.get("bin")
                .resolve(filename)

        var path = Paths.get("").toAbsolutePath()
        do {
            var binPath: Path

            binPath = path.resolve(binPlatformRelPath)
            if (Files.exists(binPath))
                return binPath.toFile()

            binPath = path.resolve(binRelPath)
            if (Files.exists(binPath))
                return binPath.toFile()

            path = path.parent
        } while (path!=null)

        return null
    }

    /**
     * Ensures all binaries within path have the executable bit/permission set
     * @param path Path
     */
    private fun setExecutablePermissions(path: File) {
        Files.walk(Paths.get(path.toURI()), 1)
                .filter { p ->
                    val filename = p.toString().toLowerCase()
                    Files.isRegularFile(p) && (!filename.contains('.') || filename.endsWith(".exe") || filename.endsWith(".dll"))
                }
                .forEach { p ->
                    // Get file attribute view
                    var fav = Files.getFileAttributeView(p, AclFileAttributeView::class.java)

                    if (fav!=null) {
                        log.debug("Verifying executable permission for [${p}]")

                        var oldAcls = fav.acl
                        var newAcls = ArrayList<AclEntry>()
                        var update = false
                        for (acl in oldAcls) {
                            // Add executable permission if it's not there yet
                            var perms = acl.permissions()
                            if (!perms.contains(AclEntryPermission.EXECUTE)) {
                                perms.add(AclEntryPermission.EXECUTE)
                                // Build new acl from old one with updated permissions
                                var aclb = AclEntry.newBuilder(acl)
                                aclb.setPermissions(perms)
                                newAcls.add(aclb.build())
                                update = true
                            } else {
                                newAcls.add(acl)
                            }
                        }
                        if (update) {
                            log.debug("Adding permission to execute to [${p}]")
                            fav.acl = newAcls
                        }
                    }
                }
    }

    /**
     * Path to executable.
     * When not set explicitly, tries to detect/find executable automatically within current and parent paths
     * */
    var file: File? = null
        @Synchronized get() {
            if (field == null) {
                log.debug("Searching for executable [${this.filename}]")
                field = this.findExecutable()
                if (field == null)
                    throw IllegalStateException("Could not find executable [${this.filename}]")

                log.debug("Found executable [${field}]")

                this.setExecutablePermissions(field!!.parentFile)
            }

            return field
        }
}