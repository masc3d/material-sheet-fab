package sx.jms.converters;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;
import sx.jms.Converter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Object message converter
 * Created by masc on 19.06.15.
 */
public class DefaultMessageConverter implements Converter {
    public enum SerializationType {
        JAVA,
        KRYO
    }

    public enum CompressionType {
        NONE,
        GZIP,
        SNAPPY
    }

    SerializationType mSerializationType;
    CompressionType mCompressionType;

    private long mBytesWritten;
    private long mBytesRead;

    private interface Serializer {
        void serialize(OutputStream outputStream, Object object) throws IOException;
    }

    private interface Deserializer {
        Object deserialize(InputStream inputStream) throws IOException, ClassNotFoundException;
    }

    private interface StreamSupplier<T> {
        T get(T stream) throws Exception;
    }

    private StreamSupplier<OutputStream> mSerializationStreamSupplier;
    private StreamSupplier<InputStream> mDeserializationStreamSupplier;
    private Serializer mSerializer;
    private Deserializer mDeserializer;

    /**
     * c'tor
     * @param serializationType
     * @param compressionType
     */
    public DefaultMessageConverter(SerializationType serializationType, CompressionType compressionType) {
        mSerializationType = serializationType;
        mCompressionType = compressionType;

        // Inject mechanisms for (de)serialization and (de)compression
        switch(mCompressionType) {
            case GZIP:
                mSerializationStreamSupplier = (o) -> new GZIPOutputStream(o);
                mDeserializationStreamSupplier = (i) -> new GZIPInputStream(i);
                break;
            case SNAPPY:
                mSerializationStreamSupplier = (o) -> new SnappyOutputStream(o);
                mDeserializationStreamSupplier = (i) -> new SnappyInputStream(i);
                break;
            default:
                mSerializationStreamSupplier = (o) -> o;
                mDeserializationStreamSupplier = (i) -> i;
        }

        switch(mSerializationType) {
            case KRYO:
                mSerializer = (outStream, o) -> {
                    Kryo k = new Kryo();
                    Output out = new Output(outStream);
                    k.writeClassAndObject(out, o);
                    out.close();
                };
                mDeserializer = (inStream) -> {
                    Kryo k = new Kryo();
                    Input in = new Input(inStream);
                    Object o = k.readClassAndObject(in);
                    in.close();
                    return o;
                };
                break;
            case JAVA:
                mSerializer = (outStream, o) -> {
                    ObjectOutputStream oos = new ObjectOutputStream(outStream);
                    oos.writeObject(o);
                    oos.close();
                };
                mDeserializer = (inStream) -> {
                    ObjectInputStream ois = new ObjectInputStream(inStream);
                    Object o = ois.readObject();
                    ois.close();
                    return o;
                };
                break;
        }
    }

    @Override
    public Message toMessage(Object object, Session session) throws JMSException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Apply intermediate stream if applicable (compression eg.) and serialize
            OutputStream serializerStream = mSerializationStreamSupplier.get(baos);
            mSerializer.serialize(serializerStream, object);

            // Create jms byte message from binary stream
            BytesMessage bm = session.createBytesMessage();
            byte[] buffer = baos.toByteArray();
            bm.writeBytes(buffer);

            mBytesWritten += buffer.length;

            return bm;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws JMSException {
        try {
            // Create binary stream from jms bytes message
            BytesMessage bm = (BytesMessage)message;
            int size = (int)bm.getBodyLength();
            byte[] buf = new byte[size];
            bm.readBytes(buf);
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);

            // Apply intermediate stream if applicable (compression eg.) and deserialize
            InputStream deserializerStream = mDeserializationStreamSupplier.get(bais);
            Object object = mDeserializer.deserialize(deserializerStream);

            mBytesRead += size;

            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getBytesWritten() {
        return mBytesWritten;
    }

    public long getBytesRead() {
        return mBytesRead;
    }

    public void resetStatistics() {
        mBytesRead = mBytesWritten = 0;
    }
}
