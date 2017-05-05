package org.deku.leoz.mobile.config

import android.content.Context
import android.content.SharedPreferences
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.singleton
import sx.android.PREFERENCE_TAG

/**
 * Created by 27694066 on 05.05.2017.
 */
class SharedPreferenceConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<SharedPreferences>() with singleton {
                val context: Context = instance()
                context.getSharedPreferences(PREFERENCE_TAG, 0)
            }
        }
    }
}