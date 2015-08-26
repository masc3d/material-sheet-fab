package org.deku.leoz.build

import java.util
import java.util.*
import kotlin.text.Regex

/**
 * Leoz artifact
 * Created by masc on 22.08.15.
 */
public data class Artifact(val type:Artifact.Type, val version: Artifact.Version) {

    /**
     * Leoz artifact type
     */
    public enum class Type(val artifactType: String) {
        LEOZ_CENTRAL("leoz-central"),
        LEOZ_NODE("leoz-node"),
        LEOZ_UI("leoz-ui"),
        LEOZ_BOOT("leoz-boot");

        override fun toString(): String {
            return this.artifactType
        }
    }

    /**
     * Artifact version
     * Created by masc on 24.08.15.
     */
    public data class Version(val components: List<Int>, val suffix: String) : Comparable<Version> {
        public companion object {
            public fun parse(version: String): Version {
                // Determine end of numeric components
                var end = version.indexOfFirst( { c -> !c.isDigit() && c != '.' } )

                var suffix: String
                if (end < 0) {
                    end = version.length()
                    suffix = ""
                } else {
                    // Cut and trim suffix
                    suffix = version.substring(end).trim { c -> c.isWhitespace() || "-._".contains(c) }
                }

                // Parse components to ints
                val components: List<Int> = if (end > 0)
                    version.substring(0, end).split('.').map( { s -> s.toInt() } )
                else
                    ArrayList<Int>()

                if (components.size() == 0)
                    throw IllegalArgumentException("Empty version string [${version}]")

                return Version(components, suffix)
            }

            public fun tryParse(version: String): Version? {
                return try { this.parse(version) } catch(e: Exception) { null }
            }
        }

        override fun compareTo(other: Version): Int {
            val tSize = this.components.size()
            val oSize = other.components.size()

            val less = if (tSize < oSize) this else other

            // Compare version components
            for (i in 0..less.components.size() - 1) {
                val c = this.components[i].compareTo(other.components[i])
                if (c != 0)
                    return c
            }

            // Revert to suffix comparison if version components were equal and both have the same amount of components
            if (tSize == oSize) {
                return this.suffix.compareTo(other.suffix)
            }

            // Otherwise the version with more components wins
            return if (tSize > oSize) 1 else -1
        }

        override fun toString(): String {
            return this.components.joinToString(".") +
                    if (this.suffix.length() > 0) "-" + suffix else ""
        }
    }
}