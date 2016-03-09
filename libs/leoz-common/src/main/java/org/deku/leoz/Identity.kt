package org.deku.leoz

import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Holds all identity information for a leoz node including system information
 * @property key Authorization key
 * @property name Name
 * @property systemInformation System information
 * Created by masc on 26.06.15.
 */
class Identity private constructor(
        val key: String,
        val name: String,
        val systemInformation: SystemInformation) {

    /**
     * Identity key
     */
    class Key(val value: String) {
        /**
         * Short key
         */
        val short: String by lazy {
            this.value.substring(0, 8)
        }

        override fun toString(): String {
            return this.value
        }
    }

    /**
     * Key
     * TODO: should become .key property/refactor all consumers carefully
     */
    val keyInstance: Key

    /**
     * Id/short key
     */
    val shortKey: String
        get() = keyInstance.short

    init {
        this.keyInstance = Key(key)
    }

    companion object {
        // Property keys for file storage
        private val PROP_NAME = "name"
        private val PROP_KEY = "key"

        /**
         * Creates an identity with a random key and current system information
         * @return
         */
        fun create(name: String, systemInformation: SystemInformation): Identity {
            try {
                // Generate key
                val sr = SecureRandom()
                val m = MessageDigest.getInstance("SHA-1")

                val hashBase = arrayOf(
                        systemInformation.hostname,
                        systemInformation.hardwareAddress,
                        systemInformation.networkAddresses.joinToString(", ")).joinToString(";")

                m.update(hashBase.toByteArray(Charsets.US_ASCII))
                val salt = ByteArray(16)
                sr.nextBytes(salt)
                m.update(salt)

                // Calculate digest and format to hex
                val key = BaseEncoding.base16().encode(m.digest()).toLowerCase()

                return Identity(key, name, systemInformation)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }


        /**
         * Read identity file
         * @param source
         * @return Identity instance
         */
        fun load(systemInfo: SystemInformation, sourceFile: File): Identity {
            val data = FilePersistence.loadMap(sourceFile)

            return Identity(
                    key = data.get(PROP_KEY) as String,
                    name = data.get(PROP_NAME) as String,
                    systemInformation = systemInfo)
        }
    }

    /**
     * Store identity to file
     * @param destination Destination file
     * @throws IOException
     */
    @Synchronized
    fun save(destinationFile: File) {
        val data = mutableMapOf<String, Any>()

        data.put(PROP_NAME, this.name)
        data.put(PROP_KEY, this.key)

        FilePersistence.dumpMap(data, destinationFile)
    }

    override fun toString(): String {
        return "Identity name [${name}] key [${key}]"
    }
}
