package org.deku.gradle

import org.apache.commons.lang3.SystemUtils

/**
 * Created by n3 on 02-Aug-15.
 */
/**
 * Common packager utils
 */
public class PackagerUtils {
    public static String archIdentifier() {
        String prefix = SystemUtils.IS_OS_WINDOWS ? "win"
                : SystemUtils.IS_OS_LINUX ? "linux"
                : SystemUtils.IS_OS_MAC_OSX ? "osx"
                : null;

        if (!prefix)
            throw IllegalStateException("Unsupported platform");

        switch (SystemUtils.OS_ARCH) {
        // 64bit
            case "amd64":
            case "x86_64": prefix += "64"; break;
        // 32bit
            case "x86": prefix += "32"; break;
        // Unsupported
            default: throw IllegalStateException("Unsupported architecture [${SystemUtils.OS_ARCH}]");
        }

        return prefix
    }
}
