package org.deku.leoz.mobile.device

import android.media.AudioFormat
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.AudioTrack.MODE_STATIC
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioFormat.CHANNEL_CONFIGURATION_MONO
import android.media.AudioTrack
import kotlin.experimental.and


/**
 * Tone
 * Created by masc on 06/03/2017.
 */
class Tone {
    val SAMPLE_RATE = 8000
    /**
     * EXPERIMNTAL. create sample for custom beep. This code needs verification & refinement.
     * @param duration Duration in milliseconds
     * @param frequency Frequency in khz
     */
    private fun createSample(duration: Int, frequency: Double): ByteArray {
        val numSamples = duration * SAMPLE_RATE / 1000
        val sample = DoubleArray(numSamples)

        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0..numSamples - 1) {
            sample[i] = Math.sin(2.0 * Math.PI * i.toDouble() / (SAMPLE_RATE / frequency))
        }

        // Convert to 16 bit PCM sound array
        // Assumes the sample buffer is normalised.
        var idx = 0
        for (dVal in sample) {
            val v: Int = (dVal * 32767).toInt()
            generatedSnd[idx++] = (v and 0x00ff).toByte()
            generatedSnd[idx++] = (v and 0xff00).ushr(8).toByte()
        }

        return generatedSnd
    }

    private fun playSample(sample: ByteArray) {
        val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                sample.size / 2,
                MODE_STATIC)
        audioTrack.write(sample, 0, sample.size / 2)
        audioTrack.play()
    }

    private val toneGenerator by lazy {
        ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
    }

    private val regularBeep by lazy {
        this.createSample(250, 1400.0)
    }

    fun beep() {
        this.toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
//        this.playSample(this.regularBeep)
    }
}