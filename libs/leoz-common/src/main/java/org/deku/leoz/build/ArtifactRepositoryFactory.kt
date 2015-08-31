package org.deku.leoz.build

import sx.rsync.Rsync
import sx.rsync.RsyncClient

/**
 * Created by masc on 22.08.15.
 */
public object ArtifactRepositoryFactory {
    private var stagingRsyncModuleUri = Rsync.URI("rsync://leoz@syntronix.de/leoz")
    private var stagingRsyncModulePassword = "leoz"

    public fun stagingRepository(artifactType: Artifact.Type): ArtifactRepository {
        return ArtifactRepository(
                type = artifactType,
                rsyncModuleUri = stagingRsyncModuleUri,
                rsyncPassword = stagingRsyncModulePassword)
    }

    public fun stagingRepository(artifact: String): ArtifactRepository {
        return this.stagingRepository(Artifact.Type.values().first { v -> v.toString().equals(artifact) })
    }
}