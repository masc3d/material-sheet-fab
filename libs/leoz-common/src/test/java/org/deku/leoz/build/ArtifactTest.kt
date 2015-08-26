package org.deku.leoz.build

import org.junit.Test

/**
 * Created by masc on 24.08.15.
 */
public class ArtifactTest {
    @Test
    public fun testVersion() {
        var v: Artifact.Version

        v = Artifact.Version.parse("1.0.1-FINAL")
        assert(v.components[0] == 1 && v.components[1] == 0 && v.components[2] == 1 && v.suffix == "FINAL")

        v = Artifact.Version.parse("1.0.10")
        assert(v.components[0] == 1 && v.components[1] == 0 && v.components[2] == 10 && v.suffix == "")
    }

    @Test
    public fun testVersionSorting() {
        var l = arrayListOf(
                Artifact.Version.parse("1.0.1-FINAL"),
                Artifact.Version.parse("1.3.1-RELEASE"),
                Artifact.Version.parse("1.3.1-FINAL"),
                Artifact.Version.parse("1.5"),
                Artifact.Version.parse("2.4.1-FINAL")
        )

        for (v in l.sort())
            println(v)
    }
}