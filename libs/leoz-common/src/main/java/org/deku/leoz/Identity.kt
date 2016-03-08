package org.deku.leoz

import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import sx.event.EventDelegate
import sx.event.EventDispatcher
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Holds all identity information for a leoz node including system information
 * @property id Node id
 * @property key Authorization key
 * @property systemInformation System information
 * Created by masc on 26.06.15.
 */
class Identity private constructor(
        id: Int?,
        val key: String,
        val name: String,
        val systemInformation: SystemInformation) {

    //region Event
    interface Listener : sx.event.EventListener {
        fun onIdUpdated(identity: Identity)
    }

    private val eventDispatcher = EventDispatcher.createThreadSafe<Listener>()
    val delegate: EventDelegate<Listener> = eventDispatcher
    //endregion

    /**
     * Id property, emitting event on update
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
        this.id = id
    }

    companion object {
        // Property keys for file storage
        private val PROP_ID = "id"
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

                return Identity(null, key, name, systemInformation)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }


        /**
         * Read identity file
         * @param source
         * @return Identity instance
         */
        fun createFromFile(systemInfo: SystemInformation, sourceFile: File): Identity {
            val data = FilePersistence.loadMap(sourceFile)

            return Identity(
                    id = data.get(PROP_ID) as Int?,
                    key = data.get(PROP_KEY) as String,
                    name = data.get(PROP_NAME) as String,
                    systemInformation = systemInfo)
        }
    }

    /**
     * Indicates if identity has an id
     * @return
     */
    fun hasId(): Boolean {
        return this.id != null
    }

    /**
     * Store identity in yml file
     * @param destination Destination file
     * @throws IOException
     */
    @Synchronized
    fun storeYml(destinationFile: File) {
        val data = mutableMapOf<String, Any>()

        val id = this.id
        if (id != null)
            data.put(PROP_ID, id)
        data.put(PROP_NAME, name)
        data.put(PROP_KEY, key)

        FilePersistence.dump(data, destinationFile)
    }

    override fun toString(): String {
        return "Identity id [${id}] name [${name}] key [${key}]"
    }
}
