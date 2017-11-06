package org.deku.leoz.mobile.ui.dialog

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.ui.Dialog

/**
 * Created by prangenberg on 06.11.17.
 */
class StopMergeDialog: Dialog(R.layout.dialog_stop_merge) {

    companion object {
        fun create(sourceStop: Stop, targetStop: Stop): StopMergeDialog {
            val dialog = StopMergeDialog()
            dialog.sourceStop = sourceStop
            dialog.targetStop = targetStop
            return dialog
        }
    }

    private lateinit var sourceStop: Stop
    private lateinit var targetStop: Stop

}