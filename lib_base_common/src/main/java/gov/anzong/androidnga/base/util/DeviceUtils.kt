package gov.anzong.androidnga.base.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Created by Justwen on 2017/7/16.
 */
object DeviceUtils {
    @JvmStatic
    fun isWifiConnected(context: Context): Boolean {
        try {
            val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = conMan.getNetworkCapabilities(conMan.activeNetwork)
            capabilities?.let {
                return it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        } catch (e: Exception) {
            // ignore
        }
        return false
    }
}
