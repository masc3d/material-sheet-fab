package sx.platform

/**
 * Platform types
 */
enum class OperatingSystem(val os: String) {
    OSX("osx"),
    WINDOWS("win"),
    LINUX("linux");

    override fun toString(): String {
        return this.os
    }
}

/**
 * Cpu architecture types
 */
enum class CpuArch(val cpuArch: String) {
    X86("32"),
    X64("64");

    override fun toString(): String {
        return this.cpuArch
    }
}
