package cc.taylorzhang.subtune.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.ui.NotificationActivity
import org.koin.android.ext.android.inject

class PlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private val musicRepository: MusicRepository by inject()

    override fun onCreate() {
        super.onCreate()
        initSessionAndPlayer()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        super.onDestroy()
    }

    @OptIn(UnstableApi::class)
    private fun initSessionAndPlayer() {
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setMediaSourceFactory(DefaultMediaSourceFactory(musicRepository.cacheDataSourceFactory))
            .build()

        val intent = Intent(this, NotificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val immutableFlag = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this, 0, intent, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
        )
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .setCallback(MediaSessionCallback(applicationContext))
            .build()
    }
}