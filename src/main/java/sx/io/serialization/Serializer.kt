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
    fun deserializeFrom(byteArray: ByteArray): Any {
        return this.deserialize(ByteArrayInputStream(byteArray))
    }

    /**
     * Register class by its @Serializable annotation/uid
     */
    fun register(cls: Class<*>): Long {
        return Serializer.types.register(cls)
    }

    /**
     * Lookup class by its @Serializable uid
     */
    fun lookup(uid: Long): Class<*>? {
        return Serializer.types.lookup(uid)
    }

    /**
     * Thread-safe @Serializable type directory
     */
    class TypeDirectory {
        private val log = LoggerFactory.getLogger(Serializer::class.java)

        private val lock = ReentrantLock()
        private val classByUid = mutableMapOf<Long,Class<*>>()
        private val uidByClass = mutableMapOf<Class<*>, Long>()

        // Readonly copy of maps or fast threadsafe access
        private var classByUidReadonly = mapOf<Long,Class<*>>()
        private var uidByClassReadonly = mapOf<Class<*>, Long>()

        /**
         * Determines UID of a class
         * @param cls Class
         * @return UID or null if class has none
         * @throws IllegalStateException When there's both @Serializable and Serializable implementation with mismatched UIDs
         */
        private fun determineUid(cls: Class<*>): Long? {
            val annotation = cls.getAnnotation(Serializable::class.java)
            val objectStreamClass = ObjectStreamClass.lookup(cls)

            val uid: Long?
            if (annotation != null) {
                uid = annotation.uid

                if (objectStreamClass != null && uid != objectStreamClass.serialVersionUID) {
                    throw IllegalStateException("Class ${cls} has mismatch @Serializable uid [${java.lang.Long.toHexString(uid)}] with serialVersionUID [${java.lang.Long.toHexString(objectStreamClass.serialVersionUID)}]")
                }
            } else {
                uid = if (objectStreamClass != null && objectStreamClass.serialVersionUID != 0L)
                    objectStreamClass.serialVersionUID
                else
                    null
            }

            return uid
        }

        // TODO: refactor `register` methods to `lookup` overloads
        /**
         * Register class with uid. If the class is already registered merely returns its UID (fast lookup)
         * @param cls Class to register
         * @param uid UID
         */
        private fun register(cls: Class<*>, uid: Long) {
            val registeredUid = this.uidByClassReadonly[cls]

            if (registeredUid != null) {
                if (registeredUid != uid)
                    throw IllegalStateException("Cannot register [${cls}] with uid [${uid}, it's already registered with [${registeredUid}}")

                return
            }

            /**
             * Helper function to register class
             * @param c Class to register
             */
            fun registerImpl(c: Class<*>, uid: Long) {
                if (uid == 0L)
                    throw IllegalArgumentException("@Serializable uid of ${c} is 0")

                this.lock.withLock {
                    log.debug("Registering type ${c}")

                    val registeredClass = this.classByUid[uid]
                    if (registeredClass != null)
                        throw IllegalArgumentException("Cannot register [${c}] as UID [${uid}] is already registered with [${registeredClass}]")

                    this.classByUid[uid] = c
                    this.uidByClass[c] = uid

                    this.classByUidReadonly = mapOf(*classByUid.toList().toTypedArray())
                    this.uidByClassReadonly = mapOf(*uidByClass.toList().toTypedArray())
                }
            }

            registerImpl(cls, uid)

            // Register nested/declared classes
            cls.declaredClasses.forEach {
                val dcuid = determineUid(it)
                if (dcuid != null)
                    registerImpl(it, dcuid)
            }
        }

        /**
         * Register class. If the class is already registered merely returns its UID (fast lookup)
         * @param cls Class to register
         * @return Class @Serializable UID or null if the type is not applicable for registering (eg. build-in type)
         */
        fun register(cls: Class<*>): Long {
            val registeredUid = this.uidByClassReadonly[cls]

            if (registeredUid != null)
                return registeredUid

            val uid = determineUid(cls)
            if (uid == null)
                throw IllegalArgumentException("Class ${cls} has neither @Serializable annotation nor implements Serializable")

            this.register(cls, uid)

            return uid
        }

        /**
         * Lookup class by uid
         * @param uid Class UID
         */
        fun lookup(uid: Long): Class<*>? {
            return classByUidReadonly[uid]
        }

        /**
         * Purges all registered classes. Useful for test cases, testing refactoring eg.
         */
        fun purge() {
            log.debug("Purging all types")
            this.lock.withLock {
                this.classByUid.clear()

                this.uidByClass.clear()
                this.classByUidReadonly = mapOf(*classByUid.toList().toTypedArray())
                this.uidByClassReadonly = mapOf(*uidByClass.toList().toTypedArray())
            }
        }
    }

    companion object {
        @JvmStatic val types = TypeDirectory()
    }
}

