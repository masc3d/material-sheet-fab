package sx.reflect

import com.esotericsoftware.reflectasm.MethodAccess
import java.beans.Introspector

/**
 * Fast reflective property accessor using reflectasm
 * Created by masc on 31.05.18.
 */
class PropertyAccess(
        val key: Key
) {
    /** Unique property key (type/name) */
    data class Key(
            val type: Class<*>,
            val name: String
    )

    private val beanInfo = Introspector.getBeanInfo(key.type)
            .propertyDescriptors
            .first { it.name == key.name }

    private val methodAccess = MethodAccess.get(key.type)

    private val writeMethodIndex = this.methodAccess.getIndex(beanInfo.writeMethod.name)
    private val readMethodIndex = this.methodAccess.getIndex(beanInfo.readMethod.name)

    fun get(instance: Any): Any? {
        return this.methodAccess.invoke(instance, this.readMethodIndex)
    }

    fun set(instance: Any, value: Any?) {
        this.methodAccess.invoke(instance, this.writeMethodIndex, value)
    }
}
