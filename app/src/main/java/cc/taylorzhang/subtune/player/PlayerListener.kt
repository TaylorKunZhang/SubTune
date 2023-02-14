package cc.taylorzhang.subtune.player

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.util.LogUtil
import cc.taylorzhang.subtune.util.ToastUtil
import kotlinx.coroutines.*

class PlayerListener(
    private val audioPlayer: AudioPlayer,
) : Player.Listener, CoroutineScope by MainScope() {

    companion object {
        private const val UPDATE_INTERVAL_MS = 200L
    }

    private var updateProgressJob: Job? = null

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        LogUtil.d("onIsPlayingChanged: $isPlaying")
        audioPlayer.updateUiState { it.copy(isPlaying = isPlaying) }
    }

    override fun onPlayerError(error: PlaybackException) {
        LogUtil.d("onPlayerError: ${error.errorCode}")
        val resId = when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> R.string.playback_net_error
            else -> null
        }
        if (resId != null) {
            ToastUtil.shortToast(resId)
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        LogUtil.d("onPlayWhenReadyChanged mediaItem=${mediaItem?.mediaMetadata?.title} reason=$reason")
        val song = if (mediaItem == null) null else audioPlayer.getSong(mediaItem.mediaId)
        audioPlayer.updateUiState { it.copy(mediaItem = mediaItem, song = song) }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        LogUtil.d("onPlayWhenReadyChanged playWhenReady=$playWhenReady reason=$reason")
        audioPlayer.updateUiState { it.copy(playWhenReady = playWhenReady) }
        if (playWhenReady) {
            updateProgress()
        } else {
            updateProgressJob?.cancel()
            updateProgressJob = null
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        LogUtil.d("onPlaybackStateChanged: $playbackState")
        if (playbackState == Player.STATE_ENDED) {
            audioPlayer.pause()
        }
    }

    private fun updateProgress() {
        updateProgressJob?.cancel()
        updateProgressJob = launch {
            while (isActive) {
                audioPlayer.refreshDurationAndPosition()
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }
}