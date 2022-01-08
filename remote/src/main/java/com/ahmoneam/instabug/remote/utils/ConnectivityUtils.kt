package com.ahmoneam.instabug.remote.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.ahmoneam.instabug.core.di.SL

class ConnectivityUtils : IConnectivityUtils {
    override val isNetworkConnected: Boolean
        get() {
            var result = false
            val cm =
                SL[Context::class.java].getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        result = when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                cm?.run {
                    cm.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            result = true
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            result = true
                        }
                    }
                }
            }
            return result
        }

    companion object {
        @JvmStatic
        inline fun checkForConnectivity(isNotConnected: (() -> Unit), isConnected: (() -> Unit)) {
            when {
                SL[IConnectivityUtils::class.java].isNetworkConnected -> isConnected()
                else -> isNotConnected()
            }
        }
    }
}