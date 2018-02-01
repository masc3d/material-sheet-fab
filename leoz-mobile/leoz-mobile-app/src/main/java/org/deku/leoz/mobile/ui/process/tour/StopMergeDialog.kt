package org.deku.leoz.mobile.ui.process.tour

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.repository.StopRepository

/**
 * Created by prangenberg on 06.11.17.
 */
class StopMergeDialog(context: Context): AlertDialog(context, R.layout.dialog_tour_stop_merge) { //Dialog(R.layout.dialog_tour_stop_merge) {

    companion object {
        private val stopRepository: StopRepository by Kodein.global.lazy.instance()

        fun create(sourceStopId: Int, targetStopId: Int, context: Context): StopMergeDialog {
            val dialog = StopMergeDialog(context)
            dialog.sourceStop = stopRepository.entities.first { it.id == sourceStopId }
            dialog.targetStop = stopRepository.entities.first { it.id == targetStopId }
            return dialog
        }
    }

    private lateinit var sourceStop: Stop
    private lateinit var targetStop: Stop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}