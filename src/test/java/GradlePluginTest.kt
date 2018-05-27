import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.IOException

import com.google.common.io.BaseEncoding
import org.junit.experimental.categories.Category
import sx.junit.StandardTest

/**
 * Prototype gradle plugin unit test (example)
 */
@Category(StandardTest::class)
class GradlePluginTest {
    private val testProjectDir = File("").absoluteFile.resolve("test/project")

    private val runner: GradleRunner by lazy {
        GradleRunner.create()
                .withProjectDir(testProjectDir.resolve("java"))
                .withPluginClasspath()
    }

    private val androidRunner: GradleRunner by lazy {
        GradleRunner.create()
                .withProjectDir(testProjectDir.resolve("android"))
                .withPluginClasspath()
    }

    @Test
    fun testBuildReleaseNativeBundleTask() {
        val result = this.runner
                .withArguments(
                        ":clean",
                        ":releaseNativeBundle")
                .build()

        println(result.output)

//        assertTrue(result.output.contains("Hello world!"))
//        assertEquals(result.task(":buildNativeBundle").outcome, SUCCESS)
    }

    @Test
    fun testBuildReleaseNativeBundleAndroidTask() {
        val result = this.androidRunner
                .withArguments(
                        ":app:clean",
                        ":app:releaseNativeBundle")
                .build()

        println(result.output)

//        assertTrue(result.output.contains("Hello world!"))
//        assertEquals(result.task(":buildNativeBundle").outcome, SUCCESS)
    }
}