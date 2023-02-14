package cc.taylorzhang.subtune.util

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Playlist
import cc.taylorzhang.subtune.model.Song

object FakeDataUtil {

    fun listMediaItems(): List<MediaItem> {
        return (0 until 15).map {
            val mediaMetadata = MediaMetadata.Builder()
                .setTitle("Song $it")
                .setArtist("Artist $it")
                .build()
            MediaItem.Builder()
                .setMediaId("$it")
                .setMediaMetadata(mediaMetadata)
                .build()
        }
    }

    fun getMediaMetadata(): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle("Song")
            .setArtist("Artist")
            .build()
    }

    fun listAlbums(): List<Album> {
        return (0 until 15).map {
            Album(id = "Album $it", name = "Album $it", artist = "Artist $it")
        }
    }

    fun getAlbum(): Album {
        return Album(id = "0", name = "Playlist").apply {
            song = listSongs()
        }
    }

    fun listPlaylists(): List<Playlist> {
        return (0 until 15).map {
            Playlist(id = "$it", name = "Playlist $it")
        }
    }

    fun getPlaylist(): Playlist {
        return Playlist(id = "0", name = "Playlist").apply {
            entry = listSongs()
        }
    }

    fun listSongs(): List<Song> {
        return (0 until 15).map {
            Song(id = "Song $it", title = "Song $it", artist = "Artist $it")
        }
    }
}