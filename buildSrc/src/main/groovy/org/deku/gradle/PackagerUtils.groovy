package org.deku.gradle

import org.apache.commons.lang3.SystemUtils

/**
 * Platform types
 */
enum Platform {
    OSX("osx"),
    WINDOWS("win"),
    LINUX("linux")

    private String mPlatform;
    public Platform(String platform) {
        mPlatform = platform;
    }

    @Override
    String toString() {
        return mPlatform;
    }
}

/**
 * Cpu architecture types
 */
enum CpuArch {
    X86("32"),
    X64("64")

    private String mCpuArch;
    public CpuArch(String cpuArch) {
        mCpuArch = cpuArch;
    }

    @Override
    String toString() {
        return mCpuArch;
    }
}

/**
 * Platform/architecture
 */
class PlatformArch {
    def Platform platform
    def CpuArch cpuArch

    public PlatformArch(Platform platform, CpuArch cpuArch) {
        this.platform = platform
        this.cpuArch = cpuArch
    }

    public static PlatformArch parse(String identifier) {
        identifier = identifier.toLowerCase()

        def regex = ~/^([a-z]+)([0-9]{2}?)$/
        def matcher = regex.matcher(identifier)

        if (!matcher.matches())
            throw new IllegalArgumentException("Could not parse arch identifier [${identifier}]")

        def sPlatform = matcher.group(1)
        def sCpuArch = matcher.group(2)

        Platform p = Platform.values().find() { it.toString().equals(sPlatform) }
        if (p == null)
            throw new IllegalArgumentException("Unknown platform identifier [${sPlatform}]")

        CpuArch ca = CpuArch.values().find() { it.toString().equals(sCpuArch) }
        if (ca == null)
            throw new IllegalArgumentException("Unknown cpu arch identifier [${sCpuArch}]")

        return new PlatformArch(p, ca)
    }

    @Override
    String toString() {
        return this.platform.toString() + this.cpuArch.toString()
    }
}

/**
 * Common packager utils
 */
class PackagerUtils {
    public static PlatformArch currentPlatformArch() {
        def Platform platform = SystemUtils.IS_OS_WINDOWS ? Platform.WINDOWS
                : SystemUtils.IS_OS_LINUX ? Platform.LINUX
                : SystemUtils.IS_OS_MAC_OSX ? Platform.OSX
                : null;

        if (platform == null)
            throw new IllegalStateException("Unsupported platform")


        def CpuArch cpuArch
        switch (SystemUtils.OS_ARCH) {
        // 64bit
            case "amd64":
            case "x86_64": cpuArch = CpuArch.X64; break;
        // 32bit
            case "x86": cpuArch = CpuArch.X86; break;
        // Unsupported
            default: throw IllegalStateException("Unsupported architecture [${SystemUtils.OS_ARCH}]");
        }

        return new PlatformArch(platform, cpuArch)
    }
}
