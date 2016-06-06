package org.deku.leoz

/**
 * Leoz global resources

 * Created by masc on 22.09.14.
 */
object Global {
    private var mInstance: Global? = null

    fun instance(): Global? {
        if (mInstance == null) {
            synchronized (Global::class.java) {
                mInstance = Global()
            }
        }
        return mInstance
    }
}
