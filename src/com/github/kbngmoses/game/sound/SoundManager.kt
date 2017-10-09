package com.github.kbngmoses.game.sound

import java.io.File
import java.net.URL
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

object SoundManager {

    abstract class Playback(val resource: String) {

        lateinit var clip: Clip
        var volume = VOL_MEDIUM

        abstract fun loop(): Boolean

        init {
            openClip()
        }

        companion object {
            val VOL_MUTE = 0
            val VOL_LOW  = 1
            val VOL_MEDIUM = 2
            val VOL_HIGH   = 3
        }

        private fun openClip() {
            val url: URL = File(resource).toURI().toURL()
            val stream = AudioSystem.getAudioInputStream(url)
            clip = AudioSystem.getClip()
            clip.open(stream)
        }

        fun play(loop: Boolean) {
            if (volume != VOL_MUTE) {
                if (clip.isRunning)
                    clip.stop()
                clip.framePosition = 0
                clip.start()

                if (loop())
                    clip.loop(Clip.LOOP_CONTINUOUSLY)
            }
        }

        fun stop() {
            clip.stop()
            clip.framePosition = 0
        }

        fun mute() {
            volume = VOL_MUTE
        }

        fun unMute() {
            volume = VOL_MEDIUM
        }
    }

    class GameStart: Playback("./resource/sound/SFX_GameStart.wav") {
        override fun loop(): Boolean = false
    }

    class ButtonUp : Playback("./resource/sound/SFX_ButtonUp.wav") {
        override fun loop(): Boolean = false
    }

    class ButtonLR: Playback("./resource/sound/SFX_PieceMoveLR.wav") {
        override fun loop(): Boolean = false
    }

    class PieceDown: Playback("./resource/sound/SLOW_HIT.wav") {
        override fun loop(): Boolean = false
    }

    class PieceRotate: Playback("./resource/sound/ROTATE.wav") {
        override fun loop(): Boolean = false
    }

    class LineRemoval4: Playback("./resource/sound/LINE_REMOVAL_4.wav") {
        override fun loop(): Boolean = false
    }

    class LineRemoval: Playback("./resource/sound/LINE_REMOVAL.wav") {
        override fun loop(): Boolean = false
    }

    class PieceRotateFail: Playback("./resource/sound/SFX_PieceRotateFail.wav") {
        override fun loop(): Boolean = false
    }

    class WonderfulVoice: Playback("./resource/sound/VO_WONDRFL.wav") {
        override fun loop(): Boolean = false
    }
}