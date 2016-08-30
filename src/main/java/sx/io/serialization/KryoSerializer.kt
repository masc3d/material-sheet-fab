package sx.io.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.pool.KryoPool
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import org.objenesis.strategy.SerializingInstantiatorStrategy
import org.objenesis.strategy.StdInstantiatorStrategy
import java.io.InputStream
import java.io.OutputStream

/**
 * Kryo serializer
 * Created by masc on 30/08/16
 */
class KryoSerializer(
        private val kryoPool: KryoPool = KryoSerializer.defaultPool)
:
        Serializer {
    /**
     * Serialize
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

    companion object {
        /**
         * Lazy kryo pool, providing and caching (soft) kryo instances
         */
        private val defaultPool by lazy {
            KryoPool.Builder({
                val k = Kryo()
                // Setting the default serializer to CompatibleFieldSerializer is crucial here
                // as the default FiedldSerializer relies solely in order and may cause breakage as classes evolve
                k.setDefaultSerializer(CompatibleFieldSerializer::class.java)
                // Required for compatibility with kryo 3.x
                k.fieldSerializerConfig.isOptimizedGenerics = true
                k
            })
                    .softReferences()
                    .build()
        }
    }
}