package org.deku.leoz.mobile.prototype.activities.smallsort

import android.os.Bundle
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Activity
import org.slf4j.LoggerFactory
import sx.android.fragment.util.withTransaction

class ProtoSsoActivity : Activity() {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        log.trace("ONCREATE")
        super.onCreate(savedInstanceState)

        this.showScreen(ProtoSsoFragment(), addToBackStack = false)
    }
}
