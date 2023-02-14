package cc.taylorzhang.subtune.util

import android.annotation.SuppressLint
import java.util.*

object FormatUtil {

    private val formatBuilder = StringBuilder()
    private val formatter = Formatter(formatBuilder)

    @SuppressLint("UnsafeOptInUsageError")
    fun getStringForTime(timeMs: Long): String {
        val millis = if (timeMs < 0) 0 else timeMs
        val totalSeconds = millis / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600
        formatBuilder.setLength(0)
        return if (hours > 0) {
            formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            formatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }
}