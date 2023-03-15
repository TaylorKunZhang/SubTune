package cc.taylorzhang.subtune.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cc.taylorzhang.subtune.data.database.dao.*
import cc.taylorzhang.subtune.data.database.entity.PagingPage
import cc.taylorzhang.subtune.data.database.entity.PlaylistWithSong
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Playlist
import cc.taylorzhang.subtune.model.Song

@Database(
    version = 2,
    entities = [
        Album::class,
        Playlist::class,
        Song::class,
        PlaylistWithSong::class,
        PagingPage::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun songDao(): SongDao

    abstract fun playlistWithSongDao(): PlaylistWithSongDao

    abstract fun pagingPageDao(): PagingPageDao
}