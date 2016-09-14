package sx.io.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Registration
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.pool.KryoPool
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.esotericsoftware.kryo.util.DefaultClassResolver
import com.esotericsoftware.kryo.util.IdentityObjectIntMap
import com.esotericsoftware.kryo.util.MapReferenceResolver
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Array
import java.util.*

/**
 * Kryo serializer with support for @Serializable annotations.
 * It's resilient against refactored class names/packages on one or the other end.
 * Created by masc on 30/08/16
 */
class KryoSerializer(
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
        var k: Kryo? = null
        var out: Output? = null
        try {
            k = kryoPool.borrow()
            out = Output(output)
            k.writeClassAndObject(out, obj)
        } finally {
            out?.close()
            if (k != null)
                kryoPool.release(k)
        }
    }

    /**
     * Deserialize
     * @param input Input stream
     * @return Deserialized object
     */
    override fun deserialize(input: InputStream): Any {
        var k: Kryo? = null
        var i: Input? = null
        try {
            k = kryoPool.borrow()
            i = Input(input)
            return k!!.readClassAndObject(i)
        } finally {
            i?.close()
            if (k != null)
                kryoPool.release(k)
        }
    }

    /**
     * Customized kryo class resolver with support for @Serializable annotations
     */
    class ClassResolver : DefaultClassResolver() {
        private val log = LoggerFactory.getLogger(this.javaClass)

        override fun writeName(output: Output?, type: Class<*>?, registration: Registration?) {
            super.writeName(output, type, registration)

            // Determine type to register. In case of arrays using componentType
            val typeToRegister = if (type!!.isArray)
                type.componentType
            else
                type

            val uid = Serializer.register(typeToRegister)

            // Write @Serializable uid to kryo output stream
            output!!.writeLong(uid)
        }

        override fun readName(input: Input?): Registration {
            val r = super.readName(input)

            // Read @Serializable uid from kryo stream
            val uid = input!!.readLong()

            // Look up class
            val cls = if (r.type.isArray) {
                Array.newInstance(Serializer.lookup(uid), 0).javaClass
            } else {
                Serializer.lookup(uid)
            }

            // Check if serializer mapped type is different
            if (cls != null && !cls.equals(r.type)) {
                log.debug("Replacing kryo mapping for ${r.type.name} with ${cls.name}")
                // Override entries in ClassResolver's lookup dictionaries
                this.nameToClass.put(r.type.name, cls)
                val key = this.nameIdToClass.findKey(r.type, true, -1)
                if (key < 0) throw IllegalStateException("Class ${r.type} not found in ClassResolver lookup dictionary")
                this.nameIdToClass.put(key, cls)

                // Return resolved registration
                return this.kryo.getRegistration(cls)
            } else {
                return r
            }
        }
    }

    companion object {
        /**
         * Kryo factory
         */
        private fun newKryo(): Kryo {
            val k = Kryo(ClassResolver(), MapReferenceResolver())
            // Setting the default serializer to CompatibleFieldSerializer is crucial here
            // as the default FiedldSerializer relies solely in order and may cause breakage as classes evolve
            k.setDefaultSerializer(CompatibleFieldSerializer::class.java)
            // Required for compatibility with kryo 3.x
            k.fieldSerializerConfig.isOptimizedGenerics = true
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