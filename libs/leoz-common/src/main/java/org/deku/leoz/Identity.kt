package org.deku.leoz

import com.google.common.base.Charsets
import com.google.common.base.Strings
import com.google.common.io.BaseEncoding
import sx.event.EventDelegate
import sx.event.EventDispatcher

import java.io.*
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Properties

/**
 * Holds all identity information for a leoz node including system information
 * @property id Node id
 * @property key Authorization key
 * @property systemInformation System information
 * Created by masc on 26.06.15.
 */
class Identity private constructor(id: Int?,
                                   val key: String?,
                                   val systemInformation: SystemInformation) {

    //region Event
    interface Listener : sx.event.EventListener {
        fun onIdUpdated(identity: Identity)
    }

    private val eventDispatcher = EventDispatcher.createThreadSafe<Listener>()
    val delegate: EventDelegate<Listener> = eventDispatcher
    //endregion

    /**
     * @return The numeric/short id of a node
     */
    var id: Int? = null
        @Synchronized set(id: Int?) {
            field = id
            this.eventDispatcher.emit { listener -> listener.onIdUpdated(this) }
        }

    /**
     * c'tor
     */
    init {
        if (Strings.isNullOrEmpty(key))
            throw IllegalArgumentException("Key cannot be null")

        this.id = id
    }

    companion object {
        // Property keys for file storage
        private val PROP_ID = "id"
        private val PROP_KEY = "key"

        /**
         * Creates an identity with a random key and current system information
         * @return
         */
        fun create(systemInformation: SystemInformation): Identity {
            try {
                // Generate key
                val sr = SecureRandom()
                val m = MessageDigest.getInstance("SHA-1")

                val hashBase = arrayOf(
                        systemInformation.hostname,
                        systemInformation.hardwareAddress,
                        systemInformation.networkAddresses.joinToString(", ")).joinToString(";")

                m.update(hashBase.getBytes(Charsets.US_ASCII))
                val salt = ByteArray(16)
                sr.nextBytes(salt)
                m.update(salt)

                // Calculate digest and format to hex
                val key = BaseEncoding.base16().encode(m.digest()).toLowerCase()

                return Identity(null, key, systemInformation)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }


        /**
         * Read identity file
         * @param source
         * @return Identity instance
         */
        fun createFromFile(systemInfo: SystemInformation, source: File): Identity {
            val p = Properties()
            p.load(FileInputStream(source))
            val id = p.getProperty(PROP_ID)
            return Identity(
                    if ((id != null)) Integer.valueOf(id) else null,
                    p.getProperty(PROP_KEY),
                    systemInfo)
        }
    }

    /**
     * INdicates if identity has an id
     * @return
     */
    fun hasId(): Boolean {
        return this.id != null
    }

    /**
     * Store identity locally
     * @param destination Destination file
     * *
     * @throws IOException
     */
    @Synchronized
    fun store(destination: File) {
        val p = Properties()

        if (id != null)
            p.put(PROP_ID, id!!.toString())
        if (key != null)
            p.put(PROP_KEY, key)

        val os = FileOutputStream(destination)
        p.store(os, "Identity")
        os.close()
    }

    override fun toString(): String {
        return "Identity id [${id}] key [${key}]"
    }
}
