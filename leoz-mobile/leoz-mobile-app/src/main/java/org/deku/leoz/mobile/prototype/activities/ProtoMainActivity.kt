package org.deku.leoz.mobile.prototype.activities

import android.os.Bundle
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Activity
import org.slf4j.LoggerFactory
import sx.android.fragment.util.withTransaction

class ProtoMainActivity : Activity() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.showScreen(ProtoMainFragment(), false)
    }
}
