package org.deku.leoz.mobile.device

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy

/**
 * Created by 27694066 on 19.10.2017.
 */
class Response(context: Context) {
    private val tones: Tones by Kodein.global.lazy.instance()
    private val vibrator = Vibrator(context)

    fun error() {
        tones.errorBeep()
        vibrator.errorVibrate()
    }

    fun warning() {
        tones.warningBeep()
        vibrator.warningVibrate()
    }

    fun acknowlegde() {
        tones.beep()
    }
}

class Vibrator(context: Context) {
    private val vibrator by lazy {
        context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
    }

    private val acknowledgeVibrateWave = longArrayOf( 0, 100 )
    private val warningVibrateWave = longArrayOf( 0, 100, 400, 100 )
    private val errorVibrateWave = longArrayOf( 0, 100, 400, 100, 400, 100 )

    fun ackknowledgeVibrate() {
        vibrate(acknowledgeVibrateWave, -1)
    }

    fun warningVibrate() {
        vibrate(warningVibrateWave, -1)
    }

    fun errorVibrate() {
        vibrate(errorVibrateWave, -1)
    }

    private fun vibrate(longArray: LongArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArray, repeat))
        } else {
            vibrator.vibrate(longArray, repeat)
        }
    }
}

