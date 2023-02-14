package cc.taylorzhang.subtune.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "playlist_song",
    primaryKeys = ["playlist_id", "song_id"],
)
data class PlaylistWithSong(
    @ColumnInfo(name = "playlist_id")
    val playlistId: String,

    @ColumnInfo(name = "song_id")
    val songId: String
)