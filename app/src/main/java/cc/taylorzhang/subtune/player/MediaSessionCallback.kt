package cc.taylorzhang.subtune.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MediaSessionCallback(
    private val appContext: Context
) : MediaSession.Callback, KoinComponent {

    private val audioPlayer: AudioPlayer by inject()

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        if (controller.packageName != appContext.packageName) {
            return Futures.immediateFailedFuture(UnsupportedOperationException())
        }

        val items = mediaItems.map {
            val uri = audioPlayer.getSongStreamUri(it.mediaId)
            if (uri == null) it else it.buildUpon().setUri(uri).build()
        }.toMutableList()
        return Futures.immediateFuture(items)
    }
}