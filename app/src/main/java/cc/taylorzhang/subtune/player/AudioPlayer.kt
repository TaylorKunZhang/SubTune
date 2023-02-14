package cc.taylorzhang.subtune.player

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.data.datastore.SettingsPreferences
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.model.NetType
import cc.taylorzhang.subtune.model.Song
import cc.taylorzhang.subtune.util.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AudioPlayer(
    private val musicRepository: MusicRepository,
    private val settingsPreferences: SettingsPreferences,
) {

    companion object {
        private const val ITEM_PREFIX = "[item]"
    }

    private var mediaController: MediaController? = null
    private val _uiState = MutableStateFlow(AudioPlayerUiState())
    val uiState = _uiState.asStateFlow()

    private var netType = NetType.MOBILE
    private val playbackList = ArrayList<Song>()

    fun setMediaController(controller: MediaController) {
        mediaController = controller
        controller.addListener(PlayerListener(this))
        initMediaControllerState()
    }

    fun updateUiState(function: (AudioPlayerUiState) -> AudioPlayerUiState) {
        _uiState.update(function)
    }

    fun setPlaybackList(songs: List<Song>, defaultPosition: Int? = null): Boolean {
        val controller = mediaController ?: return false
        controller.stop()
        controller.clearMediaItems()
        playbackList.clear()
        if (songs.isEmpty()) return true

        playbackList.addAll(songs)
        controller.addMediaItems(songs.map { buildMediaItem(it, getMaxBitrate()) })
        if (defaultPosition != null) {
            controller.seekToDefaultPosition(defaultPosition)
        }
        controller.playWhenReady = true
        controller.prepare()
        return true
    }

    fun clearPlaybackList() {
        val controller = mediaController ?: return
        controller.stop()
        controller.clearMediaItems()
        playbackList.clear()
    }

    fun removeMediaItem(index: Int) {
        val controller = mediaController ?: return
        controller.removeMediaItem(index)
        playbackList.removeAt(index)
    }

    fun play() {
        val controller = mediaController ?: return
        when (controller.playbackState) {
            Player.STATE_IDLE -> controller.prepare()
            Player.STATE_ENDED -> controller.seekTo(0)
            else -> Unit
        }
        controller.playWhenReady = true
    }

    fun pause() {
        mediaController?.playWhenReady = false
    }

    fun seekToPrevious() {
        mediaController?.seekToPrevious()
        refreshDurationAndPosition()
    }

    fun seekToNext() {
        val controller = mediaController ?: return
        if (controller.hasNextMediaItem()) {
            controller.seekToNext()
        } else if (controller.mediaItemCount > 0) {
            controller.seekTo(0, 0)
        }
        refreshDurationAndPosition()
    }

    fun seek(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        refreshDurationAndPosition()
    }

    fun seek(mediaItemIndex: Int, positionMs: Long) {
        mediaController?.seekTo(mediaItemIndex, positionMs)
        refreshDurationAndPosition()
    }

    fun togglePlaybackMode() {
        val controller = mediaController ?: return
        if (controller.shuffleModeEnabled) {
            ToastUtil.shortToast(R.string.playback_mode_in_order)
            controller.shuffleModeEnabled = false
            controller.repeatMode = MediaController.REPEAT_MODE_OFF
        } else {
            when (controller.repeatMode) {
                MediaController.REPEAT_MODE_OFF -> {
                    ToastUtil.shortToast(R.string.playback_mode_repeat)
                    controller.repeatMode = MediaController.REPEAT_MODE_ALL
                }

                MediaController.REPEAT_MODE_ALL -> {
                    ToastUtil.shortToast(R.string.playback_mode_repeat_one)
                    controller.repeatMode = MediaController.REPEAT_MODE_ONE
                }

                MediaController.REPEAT_MODE_ONE -> {
                    ToastUtil.shortToast(R.string.playback_mode_shuffle)
                    controller.repeatMode = MediaController.REPEAT_MODE_OFF
                    controller.shuffleModeEnabled = true
                }

                else -> throw NotImplementedError()
            }
        }
        refreshPlaybackMode()
    }

    fun listMediaItems(): List<MediaItem> {
        val controller = mediaController ?: return emptyList()
        val list = ArrayList<MediaItem>()
        val itemCount = controller.mediaItemCount
        for (index in 0 until itemCount) {
            list.add(controller.getMediaItemAt(index))
        }
        return list
    }

    fun getSongStreamUri(mediaId: String): Uri? {
        val song = getSong(mediaId) ?: return null
        return musicRepository.getSongStreamUri(song.id, getMaxBitrate())
    }

    fun getSong(mediaId: String): Song? {
        return playbackList.find { mediaId.contains(it.id) }
    }

    fun onNetTypeChanged(netType: NetType) {
        if (netType == NetType.NONE) return
        if (this.netType != netType) {
            this.netType = netType
            refreshMediaItems(getMaxBitrate())
        }
    }

    fun onMaxBitrateWifiChanged(value: Int) {
        if (netType == NetType.WIFI) {
            refreshMediaItems(value)
        }
    }

    fun onMaxBitrateMobileChanged(value: Int) {
        if (netType == NetType.MOBILE) {
            refreshMediaItems(value)
        }
    }

    fun refreshDurationAndPosition() {
        val controller = mediaController ?: return
        var duration = controller.contentDuration
        if (duration == C.TIME_UNSET) {
            duration = (_uiState.value.song?.duration ?: 0L) * 1000
        }
        _uiState.update {
            it.copy(
                contentDuration = duration,
                contentPosition = controller.contentPosition,
            )
        }
    }

    private fun initMediaControllerState() {
        val controller = mediaController ?: return
        _uiState.update {
            it.copy(
                playWhenReady = controller.playWhenReady,
                isPlaying = controller.isPlaying,
                mediaItem = controller.currentMediaItem,
            )
        }
        refreshDurationAndPosition()
        refreshPlaybackMode()
    }

    private fun buildMediaItem(song: Song, maxBitRate: Int): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setAlbumTitle(song.album)
            .setTitle(song.title)
            .setArtist(song.artist)
            .setGenre(song.genre)
            .setArtworkUri(musicRepository.getCoverArtUri(song.albumId))
            .build()

        return MediaItem.Builder()
            .setMediaId(ITEM_PREFIX + song.id)
            .setMediaMetadata(mediaMetadata)
            .setUri(musicRepository.getSongStreamUri(song.id, maxBitRate))
            .build()
    }

    private fun refreshPlaybackMode() {
        val controller = mediaController ?: return
        val playbackMode = if (controller.shuffleModeEnabled) {
            PlaybackMode.SHUFFLE
        } else {
            when (controller.repeatMode) {
                MediaController.REPEAT_MODE_OFF -> PlaybackMode.IN_ORDER
                MediaController.REPEAT_MODE_ALL -> PlaybackMode.REPEAT
                MediaController.REPEAT_MODE_ONE -> PlaybackMode.REPEAT_ONE
                else -> throw NotImplementedError()
            }
        }
        _uiState.update { it.copy(playbackMode = playbackMode) }
    }

    private fun refreshMediaItems(maxBitRate: Int) {
        val controller = mediaController ?: return
        if (playbackList.isEmpty()) return

        val playWhenReadyCache = uiState.value.playWhenReady
        val currentMediaItemIndex = controller.currentMediaItemIndex
        val contentPositionCache = uiState.value.contentPosition

        controller.setMediaItems(playbackList.map { buildMediaItem(it, maxBitRate) })
        controller.seekToDefaultPosition(currentMediaItemIndex)
        controller.seekTo(currentMediaItemIndex, contentPositionCache)
        controller.playWhenReady = playWhenReadyCache
        controller.prepare()
    }

    private fun getMaxBitrate(): Int {
        return when (netType) {
            NetType.MOBILE -> settingsPreferences.settingsFlow.value.maxBitrateMobile
            else -> settingsPreferences.settingsFlow.value.maxBitrateWifi
        }
    }
}