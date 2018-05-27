/**
 *
 */
package sx.concurrent;

/**
 * Task callback interface
 *
 * @author masc
 */
public abstract class TaskCallback<V> {
    /**
     * Called when the task actually starts, in the context of the task's thread
     */
    public void onStart() {
    }

    /**
     * Called on completion of the task, in the context of the task's thread
     */
    public abstract void onCompletion(V result, boolean cancelled, Exception error);
}
