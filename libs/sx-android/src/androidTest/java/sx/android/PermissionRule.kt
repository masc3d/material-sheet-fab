package sx.android

import android.os.Build
import android.support.test.InstrumentationRegistry
import android.util.Log

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * Generic permission rule for android instrumentation tests
 *
 * This is required as of Android M/6.0 and higher as applying permissions via manifest may not suffice.
 * Some permissions have to be either explicitly granted by user or via `pm grant`.
 *
 * This class conveniently wraps this into an (instrumentation) test rule.
 */
class PermissionRule(vararg permissions: String) : TestRule {
    val permissions: Array<String> = permissions.toList().toTypedArray()

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                allow()
                try {
                    base.evaluate()
                } finally {
                    revoke()
                }
            }
        }
    }

    private fun allow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                InstrumentationRegistry.getInstrumentation().uiAutomation
                        .executeShellCommand(
                                "pm grant ${InstrumentationRegistry.getTargetContext().packageName} ${permission}").use { pfd ->

                    // Synchronize with execution of command by reading the whole stream
                    InputStreamReader(FileInputStream(pfd.fileDescriptor)).readLines()
                }
            }
        }
    }

    private fun revoke() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                InstrumentationRegistry.getInstrumentation().uiAutomation
                        .executeShellCommand(
                                "pm revoke ${InstrumentationRegistry.getTargetContext().packageName} ${permission}").use { }
            }
        }
    }
}