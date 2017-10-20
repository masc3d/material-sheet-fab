package org.deku.leoz.mobile.device

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy

/**
 * Created by 27694066 on 19.10.2017.
 */
class Feedback {
    private val tones: Tones by Kodein.global.lazy.instance()
    private val vibrator: Vibrator by Kodein.global.lazy.instance()

    fun error() {
        tones.errorBeep()
        vibrator.errorVibrate()
    }

    fun warning() {
        tones.warningBeep()
        vibrator.warningVibrate()
    }

    fun acknowledge() {
        tones.beep()
    }
}

class Vibrator(context: Context) {
    private val vibrator by lazy {
        context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
    }

    private val acknowledgeVibrateWave = longArrayOf( 0, 100 )
    private val warningVibrateWave = longArrayOf( 0, 500, 50, 200, 50, 500 )
    private val errorVibrateWave = longArrayOf( 0, 1000 )

    fun ackknowledgeVibrate() {
        vibrate(acknowledgeVibrateWave)
    }

    fun warningVibrate() {
        vibrate(warningVibrateWave)
    }

    fun errorVibrate() {
        vibrate(errorVibrateWave)
    }

    private fun vibrate(longArray: LongArray, repeat: Int = -1) {
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArray, repeat))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArray, repeat)
            }
        }
    }
}

