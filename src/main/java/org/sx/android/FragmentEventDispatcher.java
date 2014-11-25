package org.sx.android;

import org.sx.util.EventListener;
import org.sx.util.RegularEventDispatcher;

/**
 * Event dispatcher for fragments, automatically dispatches events to parent fragment and activity
 * Created by masc on 25.11.14.
 */
public class FragmentEventDispatcher<T extends EventListener> extends RegularEventDispatcher<T> {
    android.support.v4.app.Fragment mSupportFragment;

    public FragmentEventDispatcher(android.support.v4.app.Fragment fragment) {
        mSupportFragment = fragment;
        this.add(fragment.getParentFragment());
        this.add(fragment.getActivity());
    }
}
