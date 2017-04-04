package sx

/**
 * Created by masc on 30/08/16.
 */
interface Copyable<T> : kotlin.Cloneable {
    public override fun clone(): Any {
        return this.copyInstance() as Any
    }
    public fun copyInstance(): T
}