package sx.io

import com.sun.jna.platform.win32.Advapi32Util
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.*
import java.util.*

/**
 * Permission utility functions
 * Created by n3 on 25-Oct-15.
 */
object PermissionUtil {
    object Win32 {
        val SID_EVERYONE = "S-1-1-0"

        /**
         * Returns FQN for a sid
         * @param sid Account SID
         */
        fun sidToFqn(sid: String): String {
            return Advapi32Util.getAccountBySid(sid).fqn
        }

        /**
         * FQN of the win32 `Everyone` account
         */
        val fqnEveryone: String
            get() = sidToFqn(SID_EVERYONE)
    }

    /**
     * Applies ACL and owner of given path to all child objects (files and folders)
     * @param path Root path
     */
    fun applyAclRecursively(path: File) {
        val rootFav = Files.getFileAttributeView(path.toPath(), AclFileAttributeView::class.java)

        path.walk().forEach { f ->
            // Get file attribute view
            val fav = Files.getFileAttributeView(f.toPath(), AclFileAttributeView::class.java)

            fav.owner = rootFav.owner
            fav.acl = rootFav.acl
        }
    }

    /**
     * Set ACL to allow everything for specific principals, replacing existing permissions
     * @param path Path
     * @param inherit Inherit permissions
     * @param principals Principals to grant access for
     */
    fun setAclAllowEverything(path: File, inherit: Boolean = true, vararg principals: String) {
        // Get file attribute view
        var fav = Files.getFileAttributeView(path.toPath(), AclFileAttributeView::class.java)

        // Lookup principal
        var fs = FileSystems.getDefault()
        var ups: UserPrincipalLookupService = fs.userPrincipalLookupService

        // Set ACL
        fav.acl = principals.map { p ->
            var gp = ups.lookupPrincipalByName(p)

            var aclb = AclEntry.newBuilder()
            aclb.setPermissions(EnumSet.allOf(AclEntryPermission::class.java))
            aclb.setPrincipal(gp)
            if (inherit)
                aclb.setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT)
            aclb.setType(AclEntryType.ALLOW)
            aclb.build()
        }
    }
}