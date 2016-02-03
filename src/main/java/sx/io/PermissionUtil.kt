package sx.io

import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.AclFileAttributeView

/**
 * Permission utility functions
 * Created by n3 on 25-Oct-15.
 */
object PermissionUtil {
    val SID_EVERYONE = "S-1-1-0"

    /**
     * Applies acl and owner of given path to all child objects (files and folders)
     * @param path Root path
     */
    fun applyAclRecursively(path: File) {
        if (SystemUtils.IS_OS_WINDOWS) {
            val rootFav = Files.getFileAttributeView(path.toPath(), AclFileAttributeView::class.java)

            path.walk().forEach { f ->
                // Get file attribute view
                val fav = Files.getFileAttributeView(f.toPath(), AclFileAttributeView::class.java)

                fav.owner = rootFav.owner
                fav.acl = rootFav.acl
            }
        }
        // TODO: add support for other platforms
    }
}