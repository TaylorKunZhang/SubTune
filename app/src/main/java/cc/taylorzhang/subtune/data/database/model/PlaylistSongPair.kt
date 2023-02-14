package cc.taylorzhang.subtune.data.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import cc.taylorzhang.subtune.data.database.entity.PlaylistWithSong
import cc.taylorzhang.subtune.model.Playlist
import cc.taylorzhang.subtune.model.Song

data class PlaylistSongPair(
    @Embedded
    val playlist: Playlist,

    @Relation(
        entity = Song::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistWithSong::class,
            parentColumn = "playlist_id",
            entityColumn = "song_id"
        )
    )
    val songs: List<Song>
)