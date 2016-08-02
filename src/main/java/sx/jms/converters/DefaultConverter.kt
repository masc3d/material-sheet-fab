package sx.jms.converters

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.pool.KryoPool
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import org.xerial.snappy.SnappyInputStream
import org.xerial.snappy.SnappyOutputStream
import sx.jms.Converter
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.jms.BytesMessage
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * Object message converter
 * Created by masc on 19.06.15.
 */
class DefaultConverter(
        var serializationType: DefaultConverter.SerializationType,
        var compressionType: DefaultConverter.CompressionType)
:
        Converter {
    enum class SerializationType {
        JAVA,
        KRYO
    }

    enum class CompressionType {
        NONE,
        GZIP,
        SNAPPY
    }

    companion object {
        /**
         * Lazy kryo pool, providing and caching (soft) kryo instances
         */
        private val kryoPool by lazy {
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

    private var serializationStreamSupplier: (o: OutputStream) -> OutputStream
    private var deserializationStreamSupplier: (i: InputStream) -> InputStream
    private var serializer: (outStream: OutputStream, o: Any) -> Unit
    private var deserializer: (inStream: InputStream) -> Any

    init {
        // Inject mechanisms for (de)serialization and (de)compression
        when (compressionType) {
            DefaultConverter.CompressionType.GZIP -> {
                serializationStreamSupplier = { o -> GZIPOutputStream(o) }
                deserializationStreamSupplier = { i -> GZIPInputStream(i) }
            }
            DefaultConverter.CompressionType.SNAPPY -> {
                serializationStreamSupplier = { o -> SnappyOutputStream(o) }
                deserializationStreamSupplier = { i -> SnappyInputStream(i) }
            }
            else -> {
                serializationStreamSupplier = { o -> o }
                deserializationStreamSupplier = { i -> i }
            }
        }

        when (serializationType) {
            DefaultConverter.SerializationType.KRYO -> {
                // Serialization using kryo
                serializer = fun(outStream, o) {
                    var k: Kryo? = null
                    var out: Output? = null
                    try {
                        k = kryoPool.borrow()
                        out = Output(outStream)
                        k.writeClassAndObject(out, o)
                    } finally {
                        if (out != null)
                            out.close()
                        if (k != null)
                            kryoPool.release(k)
                    }
                }
                deserializer = fun(inStream): Any {
                    var k: Kryo? = null
                    var i: Input? = null
                    try {
                        k = kryoPool.borrow()
                        i = Input(inStream)
                        return k!!.readClassAndObject(i)
                    } finally {
                        if (i != null)
                            i.close()
                        if (k != null)
                            kryoPool.release(k)
                    }
                }
            }

            DefaultConverter.SerializationType.JAVA -> {
                // Serialization using (standard) java object streams
                serializer = fun(outStream, o) {
                    val oos = ObjectOutputStream(outStream)
                    oos.writeObject(o)
                    oos.close()
                }
                deserializer = fun(inStream): Any {
                    val ois = ObjectInputStream(inStream)
                    val o = ois.readObject()
                    ois.close()
                    return o
                }
            }
        }
    }

    @Throws(JMSException::class)
    override fun toMessage(obj: Any, session: Session, onSize: ((size: Long) -> Unit)?): Message {
        val baos = ByteArrayOutputStream()

        // Apply intermediate stream if applicable (compression eg.) and serialize
        val serializerStream = serializationStreamSupplier(baos)
        serializer(serializerStream, obj)

        // Create jms byte message from binary stream
        val bm = session.createBytesMessage()
        val buffer = baos.toByteArray()
        bm.writeBytes(buffer)

        if (onSize != null)
            onSize(buffer.size.toLong())

        return bm
    }

    @Throws(JMSException::class)
    override fun fromMessage(message: Message, onSize: ((size: Long) -> Unit)?): Any {
        // Create binary stream from jms bytes message
        val bm = message as BytesMessage
        val size = bm.bodyLength
        val buf = ByteArray(size.toInt())
        bm.readBytes(buf)
        val bais = ByteArrayInputStream(buf)

        // Apply intermediate stream if applicable (compression eg.) and deserialize
        val deserializerStream = deserializationStreamSupplier(bais)
        val obj = deserializer(deserializerStream)

        if (onSize != null)
            onSize(size)

        return obj
    }
}
