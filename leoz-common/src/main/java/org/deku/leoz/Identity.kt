package org.deku.leoz

import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import sx.io.serialization.Serializable
import java.io.File
import java.lang.reflect.Constructor
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Holds all identity information for a leoz node including system information
 * @property key Authorization key
 * @property name Name
 * @property systemInformation System information
 * Created by masc on 26.06.15.
 */
@Serializable
class Identity private constructor(
        val key: String,
        val name: String,
        @Transient
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

        override fun hashCode(): Int {
            return this.value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other is Identity.Key)
                return this.value == other.value
            else
                return super.equals(other)
        }
    }

    /**
     * State which may be persisted
     */
    class State(
            var key: String = "",
            var name: String = "") {

        constructor(identity: Identity) : this(identity.key, identity.name) { }
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
        fun load(sourceFile: File, systemInfo: SystemInformation): Identity {
            val state = FilePersistence.load(State::class.java, sourceFile)

            return Identity(
                    key = state.key,
                    name = state.name,
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
        FilePersistence.save(State(this), destinationFile)
    }

    override fun toString(): String {
        return "Identity name [${name}] key [${key}]"
    }
}
