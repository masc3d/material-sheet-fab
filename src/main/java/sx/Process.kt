package sx

import org.apache.commons.lang3.SystemUtils
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean

/**
 * System process
 * Created by masc on 29/11/2016.
 */
class Process private constructor(
        val pid: Long,
        val name: String) {

    companion object {
        val currentProcess by lazy {
            val runtimeBean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean();

            val name = runtimeBean.getName()
            val pid = name.split("@")[0].toLong()

            Process(pid = pid,
                    name = name)
        }

        /**
         * Signals termination to a process
         */
        fun kill(pid: Long) {
            when {
                SystemUtils.IS_OS_WINDOWS -> { ProcessExecutor.run(listOf("taskkill", "/pid", pid.toString())) }
                else -> { ProcessExecutor.run(listOf("kill", pid.toString())) }
            }
        }
    }
}