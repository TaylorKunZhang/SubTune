package cc.taylorzhang.subtune.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Search3Result(
    val album: MutableList<Album> = ArrayList(),
    val song: MutableList<Song> = ArrayList(),
)