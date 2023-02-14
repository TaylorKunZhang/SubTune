package cc.taylorzhang.subtune.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Song

data class AlbumSongPair(
    @Embedded
    val album: Album,

    @Relation(
        entity = Song::class,
        parentColumn = "id",
        entityColumn = "album_id",
    )
    val songs: List<Song>
)