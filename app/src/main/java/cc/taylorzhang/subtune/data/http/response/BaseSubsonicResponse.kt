package cc.taylorzhang.subtune.data.http.response

import cc.taylorzhang.subtune.model.Error

open class BaseSubsonicResponse<T>(val data: T?) {
    var error: Error? = null
}