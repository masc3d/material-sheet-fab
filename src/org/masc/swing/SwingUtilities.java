package org.masc.swing;

public class SwingUtilities {
    /**
     * Invokes later if needed (not on a dispatch thread) or executes right away
     */
    public static void invokeLaterIfNeeded(Runnable r) {
        if (javax.swing.SwingUtilities.isEventDispatchThread())
            r.run();
        else
            javax.swing.SwingUtilities.invokeLater(r);
    }
}
