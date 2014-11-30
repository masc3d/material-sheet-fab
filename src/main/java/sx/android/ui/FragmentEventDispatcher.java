package sx.android.ui;

import sx.util.EventListener;
import sx.util.RegularEventDispatcher;

/**
 * Event dispatcher for fragments, automatically dispatches events to parent fragment and activity
 * Created by masc on 25.11.14.
 */
public class FragmentEventDispatcher<T extends EventListener> extends RegularEventDispatcher<T> {
    android.support.v4.app.Fragment mSupportFragment;

    public FragmentEventDispatcher(Class<T> c, android.support.v4.app.Fragment fragment) {
        mSupportFragment = fragment;
        if (c.isInstance(fragment.getParentFragment()))
            this.add((T)fragment.getParentFragment());
        if (c.isInstance(fragment.getActivity()))
            this.add((T)fragment.getActivity());
    }
}
