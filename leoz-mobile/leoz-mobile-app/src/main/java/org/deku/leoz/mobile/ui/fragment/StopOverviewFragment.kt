package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.fragment_stop_overview.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Job
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.StopListAdapter
import org.deku.leoz.mobile.ui.dialog.StopListDialog


/**
 * A simple [Fragment] subclass.
 */
class StopOverviewFragment : Fragment() {

    private val job: Job by Kodein.global.lazy.instance()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_stop_overview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxStopList.setOnItemClickListener { parent, view, position, id ->
            val dialog = StopListDialog(this.uxStopList.getItemAtPosition(position) as Stop)
            dialog.show(childFragmentManager, "TOURLISTDIALOG")
        }

        this.uxStopList.adapter = StopListAdapter(context, job.stopList.filter { it.state == Stop.State.PENDING })
    }

}
