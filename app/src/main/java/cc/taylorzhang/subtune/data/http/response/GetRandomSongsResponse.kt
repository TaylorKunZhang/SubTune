package cc.taylorzhang.subtune.data.http.response

import androidx.annotation.Keep
import cc.taylorzhang.subtune.model.Song
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class GetRandomSongsResponse(
    @Json(name = "subsonic-response")
    val response: SubsonicResponse
) {

    @Keep
    @JsonClass(generateAdapter = true)
    data class SubsonicResponse(
        val randomSongs: RandomSongs?
    ) : BaseSubsonicResponse<List<Song>>(randomSongs?.song)

    @Keep
    @JsonClass(generateAdapter = true)
    data class RandomSongs(
        val song: List<Song> = ArrayList()
    )
}