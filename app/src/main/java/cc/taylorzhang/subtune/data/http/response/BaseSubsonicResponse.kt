package cc.taylorzhang.subtune.data.http.response

import cc.taylorzhang.subtune.model.Error

open class BaseSubsonicResponse<T>(open val error: Error?, val data: T?)