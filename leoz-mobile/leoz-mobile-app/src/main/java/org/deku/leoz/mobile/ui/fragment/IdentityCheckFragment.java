package org.deku.leoz.mobile.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.deku.leoz.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IdentityCheckFragment extends Fragment {


    public IdentityCheckFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_identity_check, container, false);
    }

}
