package sx.io.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.KryoException
import com.esotericsoftware.kryo.Registration
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.pool.KryoPool
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.esotericsoftware.kryo.serializers.EnumNameSerializer
import com.esotericsoftware.kryo.util.*
import com.esotericsoftware.minlog.Log
import de.javakaffee.kryoserializers.UUIDSerializer
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Array
import java.util.*

/**
 * Kryo serializer with support for @Serializable annotations and lookup by UID
 * It's resilient against refactored class names/packages on one or the other end.
 * Created by masc on 30/08/16
 */
class KryoSerializer(
        /**
         * Kryo pool to use
         */
        private val kryoPool: KryoPool = KryoSerializer.defaultPool)
:
        Serializer() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Serialize
     * @param output Output stream
     * @param obj Object to serialize
     */
    override fun serialize(output: OutputStream, obj: Any) {
        val k = kryoPool.borrow()
        return try {
            Output(output).use {
                k.writeClassAndObject(it, obj)
            }
        } finally {
            kryoPool.release(k)
        }
    }

    /**
     * Deserialize
     * @param input Input stream
     * @return Deserialized object
     */
    override fun deserialize(input: InputStream): Any {
        val k = kryoPool.borrow()
        return try {
            Input(input).use {
                k.readClassAndObject(it)
            }
        } finally {
            kryoPool.release(k)
        }
    }

    /**
     * Customized kryo class resolver with support for @Serializable annotations
     */
    class ClassResolver : DefaultClassResolver() {
        private class SerializableType(
                val uid: Long,
                val isArray: Boolean
        ) {
            fun write(output: Output) {
                output.writeLong(this.uid)
                output.writeBoolean(this.isArray)
            }

            companion object {
                fun read(input: Input): SerializableType {
                    return SerializableType(
                            uid = input.readLong(),
                            isArray = input.readBoolean())
                }
            }
        }

        override fun writeName(output: Output, type: Class<*>, registration: Registration) {
            // The following code is mostly a replica of the base class logic plus support for writing @Serializable info
            output.writeVarInt(NAME + 2, true)
            if (classToNameId != null) {
                val nameId = classToNameId.get(type, -1)
                if (nameId != -1) {
                    if (Log.TRACE) Log.trace("kryo", "Write class name reference " + nameId + ": " + type.name)
                    output.writeVarInt(nameId, true)
                    return
                }
            }
            // Only write the class name the first time encountered in object graph.
            if (Log.TRACE) Log.trace("kryo", "Write class name: " + type.name)
            val nameId = nextNameId++
            if (classToNameId == null) classToNameId = IdentityObjectIntMap()
            classToNameId.put(type, nameId)
            output.writeVarInt(nameId, true)
            output.writeString(type.getName())

            //region ** Kryo protocol addition, write @Serializable metainfo **
            // Determine type to register. In case of arrays using componentType
            val typeToRegister = if (type.isArray)
                type.componentType
            else
                type

            val uid = if (typeToRegister != Any::class.java)
                Serializer.types.register(typeToRegister).uid
            else
                0L

            // Write @Serializable uid to kryo output stream
            SerializableType(uid = uid, isArray = type.isArray).write(output)
            //endregion
        }

        override fun readName(input: Input): Registration {
            // The following code is mostly a replica of the base class logic plus support for writing @Serializable info
            val nameId = input.readVarInt(true)
            if (nameIdToClass == null) nameIdToClass = IntMap()
            var type: Class<*>? = nameIdToClass.get(nameId)
            if (type == null) {
                // Only read the class name the first time encountered in object graph.
                val className = input.readString()

                //region ** Kryo protocol addition, read @Serializable metainfo **
                val serializableType = SerializableType.read(input)
                if (serializableType.uid != 0L) {
                    type = Serializer.types.lookup(serializableType.uid)?.javaClass
                    if (type != null && serializableType.isArray) {
                        type = Array.newInstance(type, 0).javaClass
                    }
                }
                //endregion

                if (type == null) {
                    type = getTypeByName(className)
                    if (type == null) {
                        try {
                            type = Class.forName(className, false, kryo.classLoader)
                        } catch (ex: ClassNotFoundException) {
                            if (Log.WARN) Log.warn("kryo", "Unable to load class $className with kryo's ClassLoader. Retrying with current..")
                            try {
                                type = Class.forName(className)
                            } catch (e: ClassNotFoundException) {
                                throw KryoException("Unable to find class: " + className, ex)
                            }

                        }

                        if (nameToClass == null) nameToClass = ObjectMap()
                        nameToClass.put(className, type)
                    }
                }
                nameIdToClass.put(nameId, type)
                if (Log.TRACE) Log.trace("kryo", "Read class name: " + className)
            } else {
                if (Log.TRACE) Log.trace("kryo", "Read class name reference " + nameId + ": " + type.name)
            }
            return kryo.getRegistration(type!!)
        }
    }

    /**
     * Customized kryo EnumNameSeriarlizer which returns null if enum value could not be mapped
     */
    class EnumNameSerializer<T>(kryo: Kryo, type: Class<T>) : com.esotericsoftware.kryo.serializers.EnumNameSerializer(kryo, type) where T : Enum<*> {
        override fun read(kryo: Kryo?, input: Input?, type: Class<Enum<*>>?): Enum<out Enum<*>>? {
            return try {
                super.read(kryo, input, type)
            } catch(e: Exception) {
                null
            }
        }
    }

    companion object {
        /**
         * Kryo factory
         */
        private fun newKryo(): Kryo {
            // Create kryo with custom ClassResolver
            val k = Kryo(ClassResolver(), MapReferenceResolver())

            // Setting the default serializer to CompatibleFieldSerializer is crucial here
            // as the default FiedldSerializer relies solely in order and may cause breakage as classes evolve
            k.setDefaultSerializer(CompatibleFieldSerializer::class.java)
            // Required for compatibility with kryo 3.x
            k.fieldSerializerConfig.isOptimizedGenerics = true

            // Register custom serializers
            k.addDefaultSerializer(Enum::class.java, EnumNameSerializer::class.java)
            k.addDefaultSerializer(UUID::class.java, UUIDSerializer::class.java)

            return k
        }

        /**
         * Lazy kryo pool, providing and caching (soft) kryo instances
         */
        private val defaultPool by lazy {
            KryoPool.Builder {
                newKryo()
            }
                    .softReferences()
                    .build()
        }
    }
}