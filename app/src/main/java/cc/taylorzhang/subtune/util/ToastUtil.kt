package cc.taylorzhang.subtune.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ToastUtil : KoinComponent {

    private val context: Context by inject()

    private var toast: Toast? = null

    fun shortToast(@StringRes resId: Int) {
        showToast(context.resources.getString(resId), Toast.LENGTH_SHORT)
    }

    fun shortToast(msg: CharSequence) {
        showToast(msg, Toast.LENGTH_SHORT)
    }

    fun longToast(@StringRes resId: Int) {
        showToast(context.resources.getString(resId), Toast.LENGTH_LONG)
    }

    fun longToast(msg: CharSequence) {
        showToast(msg, Toast.LENGTH_LONG)
    }

    private fun showToast(msg: CharSequence, duration: Int) {
        if (toast != null) {
            toast?.cancel()
            toast = null
        }
        toast = Toast.makeText(context, msg, duration)
        toast?.show()
    }
}