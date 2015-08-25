package org.deku.leoz.build

/**
 * Platform types
 */
public enum class Platform(val platform: String) {
    OSX("osx"),
    WINDOWS("win"),
    LINUX("linux");

    override fun toString(): String {
        return this.platform
    }
}

/**
 * Cpu architecture types
 */
public enum class CpuArch(val cpuArch: String) {
    X86("32"),
    X64("64");

    override fun toString(): String {
        return this.cpuArch
    }
}
