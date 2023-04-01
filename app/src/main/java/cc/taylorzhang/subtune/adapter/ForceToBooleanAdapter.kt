package cc.taylorzhang.subtune.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class ForceToBoolean

class ForceToBooleanAdapter {

    @ToJson
    fun toJson(@ForceToBoolean b: Boolean): Boolean {
        return b
    }

    @FromJson
    @ForceToBoolean
    fun fromJson(reader: JsonReader): Boolean {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> reader.nextString().toBoolean()
            JsonReader.Token.BOOLEAN -> reader.nextBoolean()
            else -> {
                reader.skipValue()
                false
            }
        }
    }
}