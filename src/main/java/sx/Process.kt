package sx

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
    }
}