package org.deku.leoz.build

import org.junit.Test
import sx.platform.PlatformId

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


    @Test
    public fun testCreate() {
        var path = ArtifactConfiguration.path.resolve(PlatformId.current().toString())

        Artifact.create(path.toFile(), Artifacts.LEOZ_BOOT, Artifact.Version.parse("0.1"))
    }

    @Test
    public fun testLoad() {
        var path = ArtifactConfiguration.path.resolve(PlatformId.current().toString())

        Artifact.load(path.toFile())
    }

    @Test
    public fun testVerify() {
        var path = ArtifactConfiguration.path.resolve(PlatformId.current().toString())

        Artifact.load(path.toFile()).verify()
    }
}