package sx.swing;

import java.awt.Component;
import java.awt.Container;

/**
 * Walks swing components recursively
 */
public class ComponentWalker {
    /**
     * Component walker action interface
     */
    public interface Action {
        boolean perform(Component c);
    }

    /**
     * Run component walker
     *
     * @param root   Root container
     * @param action Action to perform
     */
    public static void run(Container root, Action action) {
        for (Component c : root.getComponents()) {
            action.perform(c);
            if (c instanceof Container)
                run((Container) c, action);
        }
    }
}
