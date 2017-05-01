package org.deku.leoz

import sx.io.serialization.Serializable
import java.io.File

/**
 * Holds all identity information for a leoz node including system information
 * @property key Authorization key
 * @property name Name
 * @property systemInformation System information
 * Created by masc on 26.06.15.
 */
class Identity constructor(
        val key: String,
        val name: String) {

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
            if (other is Key)
                return this.value == other.value
            else
                return super.equals(other)
        }
    }

    /**
     * Persistent stat
     */
    @Serializable
    class State(
            // TODO: encrypt/decrypt key
            var key: String = "",
            var name: String = "") {

        constructor(identity: Identity) : this(identity.key, identity.name)
    }

    companion object {
        fun load(file: File): Identity {
            val state = YamlPersistence.load(State::class.java, file)

            return Identity(
                    key = state.key,
                    name = state.name)
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

    /**
     * Store identity to file
     * @param destination Destination file
     * @throws IOException
     */
    @Synchronized
    fun save(destinationFile: File) {
        YamlPersistence.save(obj = State(this), toFile = destinationFile)
    }

    override fun toString(): String {
        return "Identity name [${name}] key [${key}]"
    }
}
