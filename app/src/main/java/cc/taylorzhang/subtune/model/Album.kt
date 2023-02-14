package cc.taylorzhang.subtune.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Keep
@Entity(tableName = "album")
@JsonClass(generateAdapter = true)
data class Album(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "cover_art")
    val coverArt: String? = null,

    @ColumnInfo(name = "song_count")
    val songCount: Int = 0,

    @ColumnInfo(name = "created")
    val created: String = "",

    @ColumnInfo(name = "duration")
    val duration: Long = 0L,

    @ColumnInfo(name = "artist_id")
    val artistId: String = "",

    @ColumnInfo(name = "artist")
    val artist: String = "",
) {
    @Ignore
    var song: List<Song> = ArrayList()
}