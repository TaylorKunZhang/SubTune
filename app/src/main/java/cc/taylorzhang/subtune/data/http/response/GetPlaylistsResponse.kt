package cc.taylorzhang.subtune.data.http.response

import androidx.annotation.Keep
import cc.taylorzhang.subtune.model.Playlist
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class GetPlaylistsResponse(
    @Json(name = "subsonic-response")
    val response: SubsonicResponse
) {

    @Keep
    @JsonClass(generateAdapter = true)
    data class SubsonicResponse(
        val playlists: Playlists?
    ) : BaseSubsonicResponse<List<Playlist>>(playlists?.playlist)

    @Keep
    @JsonClass(generateAdapter = true)
    data class Playlists(
        val playlist: List<Playlist> = ArrayList()
    )
}