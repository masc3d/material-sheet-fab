//package org.deku.leoz.mobile.ui
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import kotlinx.android.synthetic.main.item_failure_reason.view.*
//import org.deku.leoz.mobile.R
//import org.deku.leoz.mobile.model.FailureReason
//
//
///**
// * Created by phpr on 04.07.2017.
// */
//class FailureReasonAdapter(val context: Context, val data: List<FailureReason>, val rootViewGroup: ViewGroup? = null): RecyclerView.Adapter<FailureReasonAdapter.ViewHolder>() {
//
//    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//    override fun onBindViewHolder(p0: ViewHolder?, p1: Int) {
//        val item: FailureReason = data[p1]
//        p0!!.reasonText.setText(item.stringRes)
//    }
//
//    override fun getItemCount(): Int {
//        return data.count()
//    }
//
//    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ViewHolder {
//        return ViewHolder(inflater.inflate(R.layout.dialog_delivery_failure_selection))
//    }
//
//    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
//        var reasonText: TextView = view.uxReasonTitle
//    }
//}