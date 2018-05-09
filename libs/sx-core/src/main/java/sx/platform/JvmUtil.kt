package sx.platform

import java.lang.management.ManagementFactory

/**
 * JVM utility classes
 * Created by masc on 03/02/16.
 */
object JvmUtil {
    private val mxBean by lazy({ ManagementFactory.getRuntimeMXBean() })
    private val runtime by lazy({ Runtime.getRuntime() })

    /**
     * JVM runtime options
     */
    val options: List<String>
        get() = mxBean.inputArguments

    /**
     * JVM maximum memory/heap size
     */
    val maxMemory: Long
        get() = runtime.maxMemory()

    /**
     * JVM version
     */
    val version: String
        get() = mxBean.vmVersion

    /**
     * Short JVM info text
     */
    val shortInfoText: String
        get() = "JVM [$version] max memory [${maxMemory /1024/1024}M]"
}