package org.deku.leoz.mobile.prototype.activities.smallsort

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.activity_proto_sso_menue.*
import org.deku.leoz.mobile.Application

import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Proto_sso_Menue_Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Proto_sso_Menue_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Proto_sso_Menue_Fragment : Fragment(), View.OnClickListener {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_proto_sso__menue_, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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

         * @return A new instance of fragment Proto_sso_Menue_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(mContext: Context): Proto_sso_Menue_Fragment {
            val fragment = Proto_sso_Menue_Fragment()
            return fragment
        }
    }

    override fun onClick(v: View) {
        val mIntent : Intent
        mListener!!.onButtonClicked(v.id)
        when (v){
            btnBagDifference -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            btnBagIncoming -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            btnBagInitialize -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            btnBagOutgoing -> {
                //mIntent = Intent(activity.applicationContext, Proto_sso_Outgoing::class.java)
                //startActivity(mIntent)

            }
            btnBagSort -> {
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
            else -> {
                log.debug("OnClick Event unhandled [$v]")
                Snackbar.make(v, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
