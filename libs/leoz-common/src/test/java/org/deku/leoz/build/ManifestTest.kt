package org.deku.leoz.build

import org.apache.commons.io.output.NullOutputStream
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by masc on 27.08.15.
 */
public class ManifestTest {
    val testPath = File("/Users/n3/Projects/customers/leoz-release/leoz-central/osx64")

    @Test
    public fun testCreate() {
        Manifest.create(testPath)
    }

    private fun serializeToString(m: Manifest): ByteArrayOutputStream {
        var b = ByteArrayOutputStream()
        m.save(b)
        return b
    }

    @Test
    public fun testSerialize() {
        var m = Manifest.create(testPath)
        println(this.serializeToString(m))
    }

    @Test
    public fun testDeserialize() {
        Manifest.load(
                ByteArrayInputStream(
                        this.serializeToString(
                                Manifest.create(testPath)).toByteArray()))
    }
}