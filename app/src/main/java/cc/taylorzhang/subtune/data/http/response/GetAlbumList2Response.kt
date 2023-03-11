package cc.taylorzhang.subtune.data.http.response

import androidx.annotation.Keep
import cc.taylorzhang.subtune.model.Album
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class GetAlbumList2Response(
    @Json(name = "subsonic-response")
    val response: SubsonicResponse
) {

    @Keep
    @JsonClass(generateAdapter = true)
    data class SubsonicResponse(
        val albumList2: AlbumList2?
    ) : BaseSubsonicResponse<List<Album>>(albumList2?.album)

    @Keep
    @JsonClass(generateAdapter = true)
    data class AlbumList2(
        val album: List<Album> = ArrayList()
    )
}