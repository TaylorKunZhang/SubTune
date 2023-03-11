package cc.taylorzhang.subtune.data.http.response

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class PingResponse(
    @Json(name = "subsonic-response")
    val response: SubsonicResponse
) {
    @Keep
    @JsonClass(generateAdapter = true)
    class SubsonicResponse : BaseSubsonicResponse<Unit>(Unit)
}