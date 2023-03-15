package cc.taylorzhang.subtune.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Keep
@Entity(tableName = "playlist")
@JsonClass(generateAdapter = true)
data class Playlist(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "song_count")
    val songCount: Int = 0,

    @ColumnInfo(name = "duration")
    val duration: Long = 0L,

    @ColumnInfo(name = "public")
    val public: Boolean = false,

    @ColumnInfo(name = "owner")
    val owner: String = "",

    @ColumnInfo(name = "created")
    val created: String = "",

    @ColumnInfo(name = "cover_art")
    val coverArt: String = "",
) {
    @Ignore
    var entry: List<Song> = ArrayList()
}