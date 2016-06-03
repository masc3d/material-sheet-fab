package org.deku.leoz

/**
 * Created by masc on 21.10.14.
 */
class Settings private constructor() {

    var isAnimationsEnabled: Boolean = false

    init {
        isAnimationsEnabled = false
    }

    companion object {
        private var mInstance: Settings? = null

        fun instance(): Settings {
            if (mInstance == null) {
                synchronized (Global::class.java) {
                    mInstance = Settings()
                }
            }
            return mInstance
        }
    }

}
