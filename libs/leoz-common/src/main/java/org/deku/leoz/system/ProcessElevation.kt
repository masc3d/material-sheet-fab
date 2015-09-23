package org.deku.leoz.system

import sx.EmbeddedExecutable

/**
 * External process for elevating processes to run with administration permissions
 * Created by masc on 23-Sep-15.
 */
object ProcessElevation {
    val executable = EmbeddedExecutable("nircmd")
}