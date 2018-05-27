package sx.event

/**
 * Multicast event delegate interface
 * Created by masc on 11.08.14.
 */
interface EventDelegate<T : EventListener> {
    /** Add listener to this multicast delegate  */
    fun add(listener: T)
    /** Remove listener from this multicast delegate  */
    fun remove(listener: T)
}
