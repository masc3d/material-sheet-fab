package org.deku.leoz.build

/**
 * Created by masc on 15.08.15.
 */
import org.apache.commons.lang3.SystemUtils
import kotlin.platform.platformStatic
import kotlin.text.Regex

/**
 * Platform/architecture
 */
public class PlatformArch(val platform: Platform, val cpuArch: CpuArch) {

    companion object {
        @platformStatic public fun parse(identifier: String): PlatformArch {
            var id  = identifier.toLowerCase()

            val regex = Regex("^([a-z]+)([0-9]{2}?)$")
            val matchResult = regex.match(id)
                    ?: throw IllegalArgumentException("Could not parse arch identifier [${identifier}]")

            var sPlatform = matchResult.groups[1]?.value
            var sCpuArch = matchResult.groups[2]?.value

            var p = Platform.values().firstOrNull() { it.toString().equals(sPlatform) }
            if (p == null)
                throw IllegalArgumentException("Unknown platform identifier [${sPlatform}]")

            var ca = CpuArch.values().firstOrNull() { it.toString().equals(sCpuArch) }
            if (ca == null)
                throw IllegalArgumentException("Unknown cpu arch identifier [${sCpuArch}]")

            return PlatformArch(p, ca)
        }

        @platformStatic public fun current(): PlatformArch {
            var platform = when {
                SystemUtils.IS_OS_WINDOWS -> Platform.WINDOWS
                SystemUtils.IS_OS_LINUX -> Platform.LINUX
                SystemUtils.IS_OS_MAC_OSX -> Platform.OSX
                else -> throw IllegalStateException("Unsupported platform")
            }

            var cpuArch = when(SystemUtils.OS_ARCH) {
                "amd64", "x86_64" -> CpuArch.X64
                "x86" -> CpuArch.X86
                else -> throw IllegalStateException("Unsupported architecture [${SystemUtils.OS_ARCH}]")
            }

            return PlatformArch(platform, cpuArch)
        }
    }

    override fun toString(): String {
        return this.platform.toString() + this.cpuArch.toString()
    }
}

