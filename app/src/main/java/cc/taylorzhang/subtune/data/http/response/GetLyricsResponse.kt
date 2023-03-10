package cc.taylorzhang.subtune.data.http.response

import androidx.annotation.Keep
import cc.taylorzhang.subtune.model.Lyrics
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class GetLyricsResponse(
    @Json(name = "subsonic-response")
    val response: SubsonicResponse
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class SubsonicResponse(val lyrics: Lyrics?) : BaseSubsonicResponse<Lyrics>(lyrics)
}