package org.deku.leoz.mobile.ui.dialog

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import kotlinx.android.synthetic.main.dialog_stop_merge.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.databinding.ItemStopBinding
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.ui.Dialog
import org.deku.leoz.mobile.ui.vm.StopViewModel

/**
 * Created by prangenberg on 06.11.17.
 */
class StopMergeDialog(context: Context): AlertDialog(context, R.layout.dialog_stop_merge) { //Dialog(R.layout.dialog_stop_merge) {

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