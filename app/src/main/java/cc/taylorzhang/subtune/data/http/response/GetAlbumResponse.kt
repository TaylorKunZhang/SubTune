package cc.taylorzhang.subtune.data.http.response

import androidx.annotation.Keep
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Error
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class GetAlbumResponse(
    @Json(name = "subsonic-response")
    val response: SubsonicResponse
) {

    @Keep
    @JsonClass(generateAdapter = true)
    data class SubsonicResponse(
        override val error: Error?,
        val album: Album?
    ) : BaseSubsonicResponse<Album>(error, album)
}