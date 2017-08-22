package sx.io.serialization

import org.slf4j.LoggerFactory
import java.io.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Lightweight serialization abstraction which makes guarantees about the type safety of the outer serialized type.
 * This is particularly useful for protocols where receivers expect multiple types of objects/messages on the same channel.
 * eg. message buses.
 *
 * The serializer embeds a type id for (at least) the outer object of the object it serializes.
 * This allows the deserializer to identify and deserialize the type properly without having precise knowledge which
 * type to expect.
 *
 * The serialization supports {@link java.io.Serializable} interface as well as the {link sx.io.Serializable} annotation.
 * Types must be registered using `Serializable.types` in order for the deserializer to recognize them.
 *
 * Type support for the following *outer* types is guaranteed:
 * <ul>
 * <li> Non-generic serializable POJOs
 * <li> Arrays of non-generic serializable POJOs
 * </ul>
 *
 * Any other types may or may not be supported by the underlying implementation.
 *
 * Created by masc on 30/08/16.
 */
abstract class Serializer {
    abstract fun serialize(output: OutputStream, obj: Any)
    abstract fun deserialize(input: InputStream): Any

    /**
     * Serializes object to byte array
     */
    fun serializeToByteArray(obj: Any): ByteArray {
        val bos = ByteArrayOutputStream()
        this.serialize(bos, obj)
        return bos.toByteArray()
    }

    /**
     * Deserializes object from byte array
     */
    fun deserializeFrom(byteArray: ByteArray): Any =
            this.deserialize(ByteArrayInputStream(byteArray))

    /**
     * Register class by its @Serializable annotation/uid
     */
    fun register(cls: Class<*>): SerializableType = Serializer.types.register(cls)

    /**
     * Lookup class by its @Serializable uid
     */
    fun lookup(uid: Long): SerializableType? = Serializer.types.lookup(uid)

    /**
     * Thread-safe @Serializable type directory
     */
    class TypeDirectory {
        private val log = LoggerFactory.getLogger(Serializer::class.java)

        private val lock = ReentrantLock()
        private val typeByUid = mutableMapOf<Long, SerializableType>()
        private val typeByClass = mutableMapOf<Class<*>, SerializableType>()

        // Readonly copy of maps or fast threadsafe access
        private var typeByUidReadonly = mapOf<Long, SerializableType>()
        private var typeByClassReadonly = mapOf<Class<*>, SerializableType>()

        // TODO: refactor `register` methods to `lookup` overloads
        /**
         * Register class with uid. If the class is already registered merely returns its UID (fast lookup)
         * @param cls Class to register
         * @param stype Serializable type
         */
        private fun register(cls: Class<*>, stype: SerializableType) {
            val registeredType = this.typeByClassReadonly[cls]

            if (registeredType != null) {
                if (registeredType.uid != stype.uid)
                    throw IllegalStateException("Cannot register [${cls}] with uid [${stype}, it's already registered with [${registeredType}}")

                return
            }

            /**
             * Helper function to register class
             * @param cls_ Class to register
             */
            fun registerImpl(cls_: Class<*>, stype_: SerializableType) {
                if (stype_.uid == 0L)
                    throw IllegalArgumentException("@Serializable uid of ${cls_} is 0")

                this.lock.withLock {
                    log.debug("Registering type ${cls_}")

                    val registeredClass = this.typeByUid[stype_.uid]
                    if (registeredClass != null)
                        throw IllegalArgumentException("Cannot register [${cls_}] as UID [${stype_}] is already registered with [${registeredClass}]")

                    this.typeByUid[stype_.uid] = stype_
                    this.typeByClass[cls_] = stype_

                    this.typeByUidReadonly = mapOf(*typeByUid.toList().toTypedArray())
                    this.typeByClassReadonly = mapOf(*typeByClass.toList().toTypedArray())
                }
            }

            registerImpl(cls, stype)

            // Register nested/declared classes
            cls.declaredClasses.forEach {
                val dstype = SerializableType.from(it)
                if (dstype != null)
                    registerImpl(it, dstype)
            }
        }

        /**
         * Register class. If the class is already registered merely returns its UID (fast lookup)
         * @param cls Class to register
         * @return Serializable type
         */
        fun register(cls: Class<*>): SerializableType {
            val registeredType = this.typeByClassReadonly[cls]

            if (registeredType != null)
                return registeredType

            val stype = SerializableType.from(cls)
            if (stype == null)
                throw IllegalArgumentException("Class ${cls} has neither @Serializable annotation nor implements Serializable")

            this.register(cls, stype)

            return stype
        }

        /**
         * Lookup serializable type by uid
         * @param uid Class UID
         */
        fun lookup(uid: Long): SerializableType? = typeByUidReadonly[uid]

        /**
         * Lookup serializable type by class
         * @param cls Class
         */
        fun typeOf(cls: Class<*>): SerializableType = typeByClassReadonly.getValue(cls)

        /**
         * Lookup serializable type by class
         * @param cls Class
         */
        fun typeOrNullOf(cls: Class<*>): SerializableType? = typeByClassReadonly.get(cls)

        /**
         * Purges all registered classes. Useful for test cases, testing refactoring eg.
         */
        fun purge() {
            log.debug("Purging all types")
            this.lock.withLock {
                this.typeByUid.clear()

                this.typeByClass.clear()
                this.typeByUidReadonly = mapOf(*typeByUid.toList().toTypedArray())
                this.typeByClassReadonly = mapOf(*typeByClass.toList().toTypedArray())
            }
        }
    }

    companion object {
        @JvmStatic val types = TypeDirectory()
    }
}

