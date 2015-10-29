package sx.jms.converters

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
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
        var mSerializationType: DefaultConverter.SerializationType,
        var mCompressionType: DefaultConverter.CompressionType)
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

    var bytesWritten: Long = 0
        private set
    var bytesRead: Long = 0
        private set

    //    private interface StreamSupplier<T> {
    //        throws(Exception::class)
    //        fun get(stream: T): T
    //    }

    private var mSerializationStreamSupplier: (o: OutputStream) -> OutputStream
    private var mDeserializationStreamSupplier: (i: InputStream) -> InputStream
    private var mSerializer: (outStream: OutputStream, o: Any) -> Unit
    private var mDeserializer: (inStream: InputStream) -> Any

    init {
        // Inject mechanisms for (de)serialization and (de)compression
        when (mCompressionType) {
            DefaultConverter.CompressionType.GZIP -> {
                mSerializationStreamSupplier = { o -> GZIPOutputStream(o) }
                mDeserializationStreamSupplier = { i -> GZIPInputStream(i) }
            }
            DefaultConverter.CompressionType.SNAPPY -> {
                mSerializationStreamSupplier = { o -> SnappyOutputStream(o) }
                mDeserializationStreamSupplier = { i -> SnappyInputStream(i) }
            }
            else -> {
                mSerializationStreamSupplier = { o -> o }
                mDeserializationStreamSupplier = { i -> i }
            }
        }

        when (mSerializationType) {
            DefaultConverter.SerializationType.KRYO -> {
                mSerializer = fun (outStream, o) {
                    val k = Kryo()
                    val out = Output(outStream)
                    k.writeClassAndObject(out, o)
                    out.close()
                }
                mDeserializer = fun (inStream): Any {
                    val k = Kryo()
                    val i = Input(inStream)
                    val o = k.readClassAndObject(i)
                    i.close()
                    return o
                }
            }
            DefaultConverter.SerializationType.JAVA -> {
                mSerializer = fun (outStream, o) {
                    val oos = ObjectOutputStream(outStream)
                    oos.writeObject(o)
                    oos.close()
                }
                mDeserializer = fun (inStream): Any {
                    val ois = ObjectInputStream(inStream)
                    val o = ois.readObject()
                    ois.close()
                    return o
                }
            }
        }
    }

    @Throws(JMSException::class)
    override fun toMessage(obj: Any, session: Session): Message {
        val baos = ByteArrayOutputStream()

        // Apply intermediate stream if applicable (compression eg.) and serialize
        val serializerStream = mSerializationStreamSupplier(baos)
        mSerializer(serializerStream, obj)

        // Create jms byte message from binary stream
        val bm = session.createBytesMessage()
        val buffer = baos.toByteArray()
        bm.writeBytes(buffer)

        bytesWritten += buffer.size.toLong()

        return bm
    }

    @Throws(JMSException::class)
    override fun fromMessage(message: Message): Any {
        // Create binary stream from jms bytes message
        val bm = message as BytesMessage
        val size = bm.bodyLength.toInt()
        val buf = ByteArray(size)
        bm.readBytes(buf)
        val bais = ByteArrayInputStream(buf)

        // Apply intermediate stream if applicable (compression eg.) and deserialize
        val deserializerStream = mDeserializationStreamSupplier(bais)
        val obj = mDeserializer(deserializerStream)

        bytesRead += size.toLong()

        return obj
    }

    fun resetStatistics() {
        bytesRead = 0
        bytesWritten = 0
    }
}
