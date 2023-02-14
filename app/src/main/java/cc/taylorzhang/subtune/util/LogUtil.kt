package cc.taylorzhang.subtune.util

import android.util.Log
import cc.taylorzhang.subtune.BuildConfig

object LogUtil {
    private const val TAG = "SubTune"

    fun d(any: Any) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, any.toString())
        }
    }

    fun i(any: Any) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, any.toString())
        }
    }

    fun e(any: Any) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, any.toString())
        }
    }
}