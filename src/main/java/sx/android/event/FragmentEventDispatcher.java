package sx.android.event;

import sx.event.EventListener;
import sx.event.RegularEventDispatcher;

/**
 * Event dispatcher for fragments, automatically dispatches events to parent fragment
 * or activity respectivly if the parent fragment does not exist or doesn't implement the listener interface
 * Created by masc on 25.11.14.
 */
public class FragmentEventDispatcher<T extends EventListener> extends RegularEventDispatcher<T> {
    android.support.v4.app.Fragment mSupportFragment;

    /**
     * Create event dispatcher from fragment.
     *
     * @param c        Listener class
     * @param fragment Fragment
     */
    public FragmentEventDispatcher(Class<T> c, android.support.v4.app.Fragment fragment) {
        mSupportFragment = fragment;
        if (c.isInstance(fragment.getParentFragment()))
            this.add((T) fragment.getParentFragment());
        else if (c.isInstance(fragment.getActivity()))
            this.add((T) fragment.getActivity());
    }
}
