package cc.taylorzhang.subtune.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Keep
@Entity(tableName = "song")
@JsonClass(generateAdapter = true)
data class Song(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "album_id")
    val albumId: String = "",

    @ColumnInfo(name = "album")
    val album: String = "",

    @ColumnInfo(name = "artist_id")
    val artistId: String? = null,

    @ColumnInfo(name = "artist")
    val artist: String = "",

    @ColumnInfo(name = "is_dir")
    val isDir: Boolean = false,

    @ColumnInfo(name = "cover_art")
    val coverArt: String = "",

    @ColumnInfo(name = "created")
    val created: String = "",

    @ColumnInfo(name = "duration")
    val duration: Long = 0L,

    @ColumnInfo(name = "bit_rate")
    val bitRate: Int = 0,

    @ColumnInfo(name = "genre")
    val genre: String? = null,

    @ColumnInfo(name = "size")
    val size: Long = 0L,

    @ColumnInfo(name = "suffix")
    val suffix: String = "",

    @ColumnInfo(name = "content_type")
    val contentType: String = "",

    @ColumnInfo(name = "is_video")
    val isVideo: Boolean = false,

    @ColumnInfo(name = "path")
    val path: String = "",

    @ColumnInfo(name = "type")
    val type: String = "",
)