package sx.io.serialization

import org.slf4j.LoggerFactory
import java.io.*
import java.math.BigInteger
import java.util.*

/**
 * Generic serialization interface
 * Using short UIDs, eg. http://www.shortguid.com/#/guid/uid-64
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
        return Serializer.register(cls)
    }

    /**
     * Lookup class by its @Serializable uid
     */
    fun lookup(uid: Long): Class<*>? {
        return Serializer.lookup(uid)
    }

    companion object {
        private val log = LoggerFactory.getLogger(Serializer::class.java)

        private val _clsByUid = mutableMapOf<Long,Class<*>>()
        private val _uidByCls = mutableMapOf<Class<*>, Long>()
        private var _readonlyClsByUid = mapOf<Long,Class<*>>()
        private var _readonlyUidByCls = mapOf<Class<*>, Long>()

        /**
         * Register class. If the class is already registered merely returns its UID (fast lookup)
         * @param cls Class to register
         * @return Class @Serializable UID
         */
        fun register(cls: Class<*>): Long {
            val registered = _readonlyUidByCls[cls]

            if (registered != null)
                return registered

            /**
             * Helper function to register class
             * @param uid Class UID
             * @param c Class to register
             */
            fun registerImpl(uid: Long, c: Class<*>) {
                // Decode short UID
                if (java.io.Serializable::class.java.isAssignableFrom(c)) {
                    val suid = ObjectStreamClass.lookup(c).serialVersionUID
                    if (suid != uid)
                        throw IllegalStateException("@Serializable uid [${java.lang.Long.toHexString(uid)}] mismatch with serialVersionUID [${java.lang.Long.toHexString(suid)}]")
                }
                synchronized(_clsByUid, {
                    log.debug("Registering type ${c.name}")
                    if (_clsByUid.containsKey(uid))
                        throw IllegalArgumentException("Cannot register [${c}] as UID [${uid}] is already registered with [${_clsByUid[uid]}]")

                    _clsByUid[uid] = c
                    _uidByCls[c] = uid
                    _readonlyClsByUid = mapOf(*_clsByUid.toList().toTypedArray())
                    _readonlyUidByCls = mapOf(*_uidByCls.toList().toTypedArray())
                })
            }

            val annotation = cls.getAnnotation(Serializable::class.java) ?: throw IllegalArgumentException("Class ${cls} is missing @Serializable")

            // Register the class
            registerImpl(annotation.uid, cls)

            // Register nested/declared classes
            cls.declaredClasses.forEach {
                val a = it.getAnnotation(Serializable::class.java)
                if (a != null) {
                    registerImpl(a.uid, it)
                }
            }

            return annotation.uid
        }

        /**
         * Lookup class by uid
         * @param uid Class UID
         */
        fun lookup(uid: Long): Class<*>? {
            return _readonlyClsByUid[uid]
        }

        /**
         * Purges all registered classes. Useful for test cases, testing refactoring eg.
         */
        fun purge() {
            log.debug("Purging all types")
            synchronized(_clsByUid, {
                _clsByUid.clear()
                _uidByCls.clear()
                _readonlyClsByUid = mapOf(*_clsByUid.toList().toTypedArray())
                _readonlyUidByCls = mapOf(*_uidByCls.toList().toTypedArray())
            })
        }
    }
}

