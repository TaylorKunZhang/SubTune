package cc.taylorzhang.subtune.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Lyrics(
    val artist: String?,
    val title: String?,
    val value: String,
)