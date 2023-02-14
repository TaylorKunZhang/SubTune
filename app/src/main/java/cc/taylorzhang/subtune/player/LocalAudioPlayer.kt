package cc.taylorzhang.subtune.player

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAudioPlayer = staticCompositionLocalOf<AudioPlayer> {
    error("LocalAudioPlayer")
}