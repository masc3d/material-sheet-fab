package sx.packager.gradle

import org.gradle.api.tasks.TaskAction
import sx.packager.Bundle
import sx.platform.CpuArch
import sx.platform.OperatingSystem
import sx.platform.PlatformId

import java.nio.file.Files

class ReleaseNativeBundleAndroidTask extends ReleaseTask {
    String group = GROUP_PACKAGER_NATIVE

    @TaskAction
    releaseNativeAndroidBundle() {
        def variantName = "${this.extension.androidExtension.productFlavor}${this.extension.androidExtension.buildType.capitalize()}".uncapitalize()

        def applicationVariant = project.android.applicationVariants.find {
            it.name == variantName
        }

        if (applicationVariant == null) {
            throw new IllegalArgumentException("Could not find application variant [${variantName}]")
        }

        // First output of variant
        def variantOutput = applicationVariant.outputs.first()
        def platform = new PlatformId(OperatingSystem.ANDROID, CpuArch.ANY)

        this.getReleasePlatformPath(platform)
                .deleteDir()

        Files.copy(
                variantOutput.outputFile.toPath(),
                this.getReleasePlatformPath(platform).toPath().resolve("${this.extension.bundleName}-${this.extension.version}.apk"))

        println "Creating bundle manifest"
        Bundle.create(
                this.getReleasePlatformPath(platform),
                this.extension.bundleName,
                platform,
                Bundle.Version.parse(this.extension.version))
    }
}