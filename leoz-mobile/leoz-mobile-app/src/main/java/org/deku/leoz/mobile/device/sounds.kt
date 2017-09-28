package org.deku.leoz.mobile.device

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import org.deku.leoz.mobile.R

/**
 * Sound container
 * Created by masc on 27.11.14.
 */
class Sounds(private val context: Context) {

    private val soundPool: SoundPool
    private val cameraSoundId: Int

    init {
        soundPool = SoundPool(
                5,
                AudioManager.STREAM_MUSIC,
                0)

        cameraSoundId = soundPool.load(context, R.raw.sound_camera_click, 0)
    }

    private fun playSoundWithId(id: Int) {
        soundPool.play(id, 1F, 1F, 0, 0, 1F)
    }

    fun playCameraClick() {
        this.playSoundWithId(cameraSoundId)
    }
}
