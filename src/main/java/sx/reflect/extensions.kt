package sx.reflect

import org.apache.commons.lang3.ClassUtils
import java.lang.reflect.Type

/**
 * All generic interfaces of a class
 * Created by masc on 12.05.17.
 */
val Class<*>.allGenericInterfaces: List<Type>
    get() {
        val interfaces = mutableListOf<Type>()

        fun recurse(c: Class<*>, types: MutableList<Type>) {
            types.addAll(c.genericInterfaces.toList())
            if (c.superclass != null && c.superclass != Any::class.java)
                recurse(c.superclass, types)
        }

        recurse(this, interfaces)

        return interfaces
    }

/**
 * All interfaces of a class
 * Created by masc on 12.05.17.
 */
val Class<*>.allInterfaces: List<Class<*>>
    get() = ClassUtils.getAllInterfaces(this)