package org.deku.leoz.mobile.ui.dialog

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.dialog_whatsnew.*
import kotlinx.android.synthetic.main.dialog_whatsnew.view.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.ui.ChangelogItem
import org.deku.leoz.mobile.ui.Dialog
import sx.LazyInstance

/**
 * Created by phpr on 24.06.2017.
 */
class ChangelogDialog: Dialog(R.layout.dialog_whatsnew) {

    private lateinit var items: List<ChangelogItem>

    private val flexibleAdapterInstance = LazyInstance<FlexibleAdapter<ChangelogItem>>({
        FlexibleAdapter(
                // Items
                items,
                // Listener
                this)
    })
    private val flexibleAdapter get() = flexibleAdapterInstance.get()

    companion object {
        fun create(items: List<ChangelogItem>): ChangelogDialog {
            val d = ChangelogDialog()
            d.items = items
            return d
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        val builder = AlertDialog.Builder(context)

        builder.setView(this.builderView)
                .setNeutralButton(R.string.ok, { dialog, which ->
                    this.dismiss()
                })
                .setCancelable(false)
        // Flexible adapter needs to be re-created with views
        flexibleAdapterInstance.reset()

        builderView.uxChangeList.adapter = flexibleAdapter
        builderView.uxChangeList.layoutManager = LinearLayoutManager(context)
        //this.uxStopList.addItemDecoration(dividerItemDecoration)

        flexibleAdapter.isLongPressDragEnabled = false
        flexibleAdapter.isHandleDragEnabled = false
        flexibleAdapter.isSwipeEnabled = false

        return builder.create()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}