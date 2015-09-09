package org.deku.leoz.build

import sx.rsync.Rsync
import sx.rsync.RsyncClient

/**
 * Created by masc on 22.08.15.
 */
public object ArtifactRepositoryFactory {
    private var stagingRsyncModuleUri = Rsync.URI("rsync://leoz@syntronix.de/leoz")
    private var stagingRsyncModulePassword = "leoz"

    public fun stagingRepository(artifactName: String): ArtifactRepository {
        return ArtifactRepository(
                name = artifactName,
                rsyncModuleUri = stagingRsyncModuleUri,
                rsyncPassword = stagingRsyncModulePassword)
    }
}