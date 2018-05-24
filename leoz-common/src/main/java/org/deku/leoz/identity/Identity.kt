package org.deku.leoz.identity

import org.deku.leoz.YamlPersistence
import sx.io.serialization.Serializable
import java.io.File
import java.util.*

/**
 * Holds all identity information for a leoz node including system information
 * @property uid Authorization key
 * @property name Name
 * @property systemInformation System information
 * Created by masc on 26.06.15.
 */
class Identity constructor(
        uid: String,
        val name: String) {

    /**
     * Identity key
     */
    class Uid(val value: String) {
        /**
         * Short key
         */
        val short: String by lazy {
            this.value.substring(0, SHORT_UID_LENGTH)
        }

        override fun toString(): String = this.value

        override fun hashCode(): Int = this.value.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other is Uid)
                return this.value == other.value
            else
                return super.equals(other)
        }

        val uuid: UUID by lazy { UUID.fromString(this.value) }
    }

    /**
     * Persistent state
     */
    @Serializable
    class State(
            /** Legacy key field for backwards cmopat. TODO: phase out */
            var key: String? = null,
            var uid: String? = null,
            var name: String? = null) {

        constructor(identity: Identity) : this(
                uid = identity.uid.value,
                name = identity.name)
    }

    companion object {
        /** The short UID length */
        val SHORT_UID_LENGTH = 8

        fun load(file: File): Identity {
            val state = YamlPersistence.load(State::class.java, file)

            return Identity(
                    uid = state.uid ?: state.key ?: "",
                    name = state.name ?: "")
        }
    }

    /**
     * Identity uid
     */
    val uid: Uid = Uid(uid)

    /**
     * Id/short key
     */
    val shortUid: String
        get() = this.uid.short

    init {
        if (uid.length < SHORT_UID_LENGTH)
            throw IllegalArgumentException("Identity uid must have at least length > 8 for short uid representation")
    }

    val uuid: UUID
        get() = this.uid.uuid

    /**
     * Store identity to file
     * @param destination Destination file
     * @throws IOException
     */
    @Synchronized
    fun save(destinationFile: File) {
        YamlPersistence.save(obj = State(this), toFile = destinationFile, skipNulls = true)
    }

    override fun toString(): String = "Identity name [${this.name}] uid [${this.shortUid}]"
}

/** Transform generic uuid to identity uid */
fun UUID.toIdentityUid(): Identity.Uid = Identity.Uid(value = this.toString())
