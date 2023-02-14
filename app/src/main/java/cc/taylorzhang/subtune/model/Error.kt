package cc.taylorzhang.subtune.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Error(
    val code: Int,
    val message: String,
)
