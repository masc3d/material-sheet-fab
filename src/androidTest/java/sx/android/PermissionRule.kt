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
 * This is required as of Android M/6.0 and higher as applying permissions via manifest is not
 * enough to some permissions, they have to explicitly granted by the user or if this is not possible
 * (eg. for instrumentation tests) via `pm grant`.
 *
 * This class conveniently wraps this into a test rule which can be easily applied for any permission required.
 */
class PermissionRule(vararg permissions: String) : TestRule {
    val permissions: Array<String>

    init {
        this.permissions = permissions.toList().toTypedArray()
    }

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                allowPermissions()
                try {
                    base!!.evaluate()
                } finally {
                    revokePermissions()
                }
            }
        }

    }

    private fun allowPermissions() {
        Log.i("", "ALLOW")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                val pf = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                        "pm grant ${InstrumentationRegistry.getTargetContext().packageName} ${permission}")

                // .executeShellCommand is asynchronous. Need to read the stream
                InputStreamReader(FileInputStream(pf.fileDescriptor)).readLines()
                pf.close()
            }
        }
    }

    private fun revokePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                val pf = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                        "pm revoke ${InstrumentationRegistry.getTargetContext().packageName} ${permission}")

                pf.close()
            }
        }
    }
}