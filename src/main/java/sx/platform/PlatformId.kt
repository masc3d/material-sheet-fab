package sx.platform

/**
 * Created by masc on 15.08.15.
 */
import org.apache.commons.lang3.SystemUtils
import sx.platform.CpuArch
import sx.platform.OperatingSystem
import kotlin.text.Regex

/**
 * Platform/architecture
 */
class PlatformId(val operatingSystem: OperatingSystem, val cpuArch: CpuArch) {
    /** Adapter for xml serialization */
    class XmlAdapter : javax.xml.bind.annotation.adapters.XmlAdapter<String, PlatformId>() {
        override fun marshal(p: PlatformId?): String? {
            return p.toString()
        }

        override fun unmarshal(v: String?): PlatformId? {
            return if (v == null) null else PlatformId.parse(v)
        }
    }

    companion object {
        @JvmStatic fun parse(identifier: String): PlatformId {
            var id  = identifier.toLowerCase()

            val regex = Regex("^([a-z]+)([0-9]{2}?)$")
            val matchResult = regex.find(id)

            if (matchResult == null)
                throw IllegalArgumentException("Invalid platform id [${identifier}]")

            var sPlatform = matchResult.groups[1]?.value
            var sCpuArch = matchResult.groups[2]?.value

            var p = OperatingSystem.values.firstOrNull() { it.toString().equals(sPlatform) }
            if (p == null)
                throw IllegalArgumentException("Unknown platform identifier [${sPlatform}]")

            var ca = CpuArch.values.firstOrNull() { it.toString().equals(sCpuArch) }
            if (ca == null)
                throw IllegalArgumentException("Unknown cpu arch identifier [${sCpuArch}]")

            return PlatformId(p, ca)
        }

        @JvmStatic fun current(): PlatformId {
            var platform = when {
                SystemUtils.IS_OS_WINDOWS -> OperatingSystem.WINDOWS
                SystemUtils.IS_OS_LINUX -> OperatingSystem.LINUX
                SystemUtils.IS_OS_MAC_OSX -> OperatingSystem.OSX
                else -> throw IllegalStateException("Unsupported platform")
            }

            var cpuArch = when(SystemUtils.OS_ARCH) {
                "amd64", "x86_64" -> CpuArch.X64
                "x86" -> CpuArch.X86
                else -> throw IllegalStateException("Unsupported architecture [${SystemUtils.OS_ARCH}]")
            }

            return PlatformId(platform, cpuArch)
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is PlatformId) this.cpuArch == other.cpuArch && this.operatingSystem == other.operatingSystem
        else false
    }

    override fun toString(): String {
        return this.operatingSystem.toString() + this.cpuArch.toString()
    }
}

