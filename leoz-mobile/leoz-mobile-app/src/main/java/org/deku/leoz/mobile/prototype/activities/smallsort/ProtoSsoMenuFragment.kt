package org.deku.leoz.mobile.prototype.activities.smallsort

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.proto_fragment_sso_menu.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Fragment
import org.slf4j.LoggerFactory

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProtoSsoMenuFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProtoSsoMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProtoSsoMenuFragment : Fragment<Any>(), View.OnClickListener {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.proto_fragment_sso_menu, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val parent = this.parentFragment
        if (parent is OnFragmentInteractionListener) {
            listener = parent as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        uxBagDifference.setOnClickListener(this)
        uxBagIncoming.setOnClickListener(this)
        uxBagInitialize.setOnClickListener(this)
        uxBagOutgoing.setOnClickListener(this)
        uxBagSort.setOnClickListener(this)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)

        fun onButtonClicked(buttonID: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment ProtoSsoMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): ProtoSsoMenuFragment {
            val fragment = ProtoSsoMenuFragment()
            return fragment
        }
    }

    override fun onClick(v: View) {
        val mIntent: Intent
        listener!!.onButtonClicked(v.id)
        when (v) {
            uxBagDifference -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            uxBagIncoming -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            uxBagInitialize -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            uxBagOutgoing -> {
                //mIntent = Intent(activity.applicationContext, ProtoSsoOutgoingFragment::class.java)
                //startActivity(mIntent)

            }
            uxBagSort -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            else -> {
                log.debug("OnClick Event unhandled [$v]")
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
