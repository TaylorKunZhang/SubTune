package cc.taylorzhang.subtune.data.http.response

import androidx.annotation.Keep
import cc.taylorzhang.subtune.model.Error
import cc.taylorzhang.subtune.model.Search3Result
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
class Search3Response(
    @Json(name = "subsonic-response")
    val response: SubsonicResponse
) {

    @Keep
    @JsonClass(generateAdapter = true)
    data class SubsonicResponse(
        override val error: Error?,
        val searchResult3: Search3Result?
    ) : BaseSubsonicResponse<Search3Result>(error, searchResult3)
}