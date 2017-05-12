package sx.reflect

import java.lang.reflect.Type

/**
 * All generic interfaces of a class
 * Created by masc on 12.05.17.
 */
val Class<*>.allGenericInterfaces: List<Type>
    get() {
        val interfaces = mutableListOf<Type>()

        fun recurse(clz: Class<*>, interfaces: MutableList<Type>) {
            interfaces.addAll(clz.genericInterfaces.toList())
            if (clz.superclass != null && clz.superclass != Any::class.java)
                recurse(clz.superclass, interfaces)
        }

        recurse(this, interfaces)

        return interfaces
    }