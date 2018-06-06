package sx.reflect

import org.apache.commons.lang3.ClassUtils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

data class GenericInterface(
        val type: Type,
        val implementingType: Class<*>
) {
    val parameterizedType = type as? ParameterizedType
}

/**
 * All generic interfaces of a class
 * Created by masc on 12.05.17.
 */
val Class<*>.allGenericInterfaces: List<GenericInterface>
    get() {
        val types = mutableListOf<GenericInterface>()

        fun recurse(c: Class<*>, types: MutableList<GenericInterface>) {
            types.addAll(c.genericInterfaces.map { GenericInterface(it, c) }.toList())
            if (c.superclass != null && c.superclass != Any::class.java)
                recurse(c.superclass, types)
        }

        recurse(this, types)

        return types
    }

/**
 * All interfaces of a class
 * Created by masc on 12.05.17.
 */
val Class<*>.allInterfaces: List<Class<*>>
    get() = ClassUtils.getAllInterfaces(this)