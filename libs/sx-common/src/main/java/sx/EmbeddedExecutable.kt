package sx

import org.apache.commons.lang3.SystemUtils
import org.slf4j.LoggerFactory
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.AclEntry
import java.nio.file.attribute.AclEntryPermission
import java.nio.file.attribute.AclFileAttributeView
import java.nio.file.attribute.PosixFilePermission
import java.util.*

/**
 * Support class for embedded executables/processes.
 * Supports automatic search for executable in parent paths, setting executable file permissions.
 * Created by masc on 21-Sep-15.
 */
class EmbeddedExecutable(
        /** Rsync executable base filename */
        var baseFilename: String,
        /** Optional fallback */
        val fallback: (() -> File?)? = null) {

    val log = LoggerFactory.getLogger(EmbeddedExecutable::class.java)

    /** Rsync executable file name */
    val filename: String by lazy {
        this.baseFilename + if (SystemUtils.IS_OS_WINDOWS) ".exe" else ""
    }

    /**
     * Find rsync executabe
     */
    private fun findExecutable(): File? {
        // Search for executable in current and parent paths
        val relativePaths = arrayOf(
                Paths.get("platform")
                        .resolve(PlatformId.current().toString())
                        .resolve("bin")
                        .resolve(filename),
                Paths.get("platform")
                        .resolve("bin")
                        .resolve(filename)
        )

        var path = Paths.get("").toAbsolutePath()
        do {
            relativePaths.forEach {
                val binPath = path.resolve(it)
                if (Files.exists(binPath))
                    return binPath.toFile()
            }

            path = path.parent
        } while (path != null)

        return null
    }

    /**
     * Ensures all binaries within path have the executable bit/permission set
     * @param path Path
     */
    private fun setExecutablePermissions(path: File) {
        Files.walk(Paths.get(path.toURI()), 1)
                .filter { p ->
                    val filename = p.fileName.toString().toLowerCase()

                    log.debug("Filename [${filename}]")

                    // Filter on relevant / potentially executable files
                    Files.isRegularFile(p) &&
                            (!filename.contains('.') ||
                                    filename.endsWith(".exe") ||
                                    filename.endsWith(".dll"))
                }
                .forEach { p ->
                    log.debug("Verifying executable permission for [${p}]")

                    when {
                        SystemUtils.IS_OS_WINDOWS -> {
                            // Get file attribute view
                            val fav = Files.getFileAttributeView(p, AclFileAttributeView::class.java)

                            if (fav != null) {

                                val oldAcls = fav.acl
                                val newAcls = ArrayList<AclEntry>()
                                var update = false
                                for (acl in oldAcls) {
                                    // Add executable permission if it's not there yet
                                    val perms = acl.permissions()
                                    if (!perms.contains(AclEntryPermission.EXECUTE)) {
                                        perms.add(AclEntryPermission.EXECUTE)
                                        // Build new acl from old one with updated permissions
                                        val aclb = AclEntry.newBuilder(acl)
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
                        SystemUtils.IS_OS_LINUX -> {
                            val posixPermissions = Files.getPosixFilePermissions(p)
                            posixPermissions.addAll(listOf(
                                    PosixFilePermission.OWNER_EXECUTE,
                                    PosixFilePermission.GROUP_EXECUTE,
                                    PosixFilePermission.OTHERS_EXECUTE
                            ))
                            Files.setPosixFilePermissions(
                                    p,
                                    posixPermissions
                            )
                        }
                    }
                }
    }

    /**
     * Path to executable.
     * When not set explicitly, tries to detect/find executable automatically within current and parent paths
     * @throws IllegalStateException If embedded executable could not be found
     * */
    var file: File? = null
        @Synchronized get() {
            if (field == null) {
                log.debug("Searching for executable [${this.filename}]")
                field = this.findExecutable()
                if (field != null) {
                    // Ensure executability for embedded rsync binaries
                    this.setExecutablePermissions(field!!.parentFile)
                } else {
                    if (this.fallback != null) {
                        // Fallback to system rsync on linux
                        log.warn("Could not find executable [${filename}], attempting fallback")
                        val fallbackExecutable = this.fallback.invoke()
                        if (fallbackExecutable != null && !fallbackExecutable.exists())
                            throw IllegalStateException("Fallback executable [${fallbackExecutable}] does not exist")

                        field = fallbackExecutable
                    }
                }

                if (field == null)
                    throw IllegalStateException("Could not find executable [${this.filename}]")

                log.debug("Using executable [${field}]")
            }

            return field
        }
}