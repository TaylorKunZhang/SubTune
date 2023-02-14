package cc.taylorzhang.subtune.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import cc.taylorzhang.subtune.model.NetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NetworkUtil {

    private val innerNetTypeFlow = MutableStateFlow(NetType.NONE)
    val netTypeFlow = innerNetTypeFlow.asStateFlow()

    fun onAppStart(context: Context) {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()
        manager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {
                super.onLost(network)
                checkNetwork(manager)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                checkNetwork(manager)
            }
        })
    }

    private fun checkNetwork(manager: ConnectivityManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = manager.activeNetwork
            if (activeNetwork == null) {
                innerNetTypeFlow.value = NetType.NONE
                return
            }
            val capabilities = manager.getNetworkCapabilities(activeNetwork)
            innerNetTypeFlow.value = when {
                capabilities == null -> NetType.NONE
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetType.WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetType.MOBILE
                else -> NetType.NONE
            }
        } else {
            val networkInfo = manager.activeNetworkInfo
            innerNetTypeFlow.value = when {
                networkInfo == null -> NetType.NONE
                networkInfo.type == ConnectivityManager.TYPE_WIFI -> NetType.WIFI
                networkInfo.type == ConnectivityManager.TYPE_MOBILE -> NetType.MOBILE
                else -> NetType.NONE
            }
        }
    }
}