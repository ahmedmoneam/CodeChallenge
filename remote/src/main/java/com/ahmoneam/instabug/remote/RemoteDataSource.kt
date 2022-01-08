package com.ahmoneam.instabug.remote

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.AppException
import com.ahmoneam.instabug.core.error.NetworkErrorType
import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.core.threading.CustomFutureTask
import com.ahmoneam.instabug.core.threading.Threading.execute
import com.ahmoneam.instabug.remote.utils.IConnectivityUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.InterruptedIOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

class RemoteDataSource : IRemoteDataSource {
    override val connectivityUtils: IConnectivityUtils get() = SL[IConnectivityUtils::class.java]

    override fun getHtmlPage(url: String): CustomFutureTask<Result<String>> {
        return execute {
            if (!connectivityUtils.isNetworkConnected)
                return@execute Result.Failure(AppException(NetworkErrorType.NoInternetConnection))

            var urlConnection: HttpURLConnection? = null
            try {
                val url1 = URL(url)
                urlConnection = url1.openConnection() as HttpURLConnection
                when (val code = urlConnection.responseCode) {
                    in 200..227 -> {
                        val inputStream = urlConnection.inputStream
                        val inputStreamReader = InputStreamReader(inputStream)
                        val bufferedReader = BufferedReader(inputStreamReader)
                        var inputLine: String?
                        val stringBuilder = StringBuilder()
                        while (bufferedReader.readLine().also { inputLine = it } != null) {
                            stringBuilder.append(inputLine)
                        }
                        bufferedReader.close()
                        Result.Success(stringBuilder.toString())
                    }
                    else -> Result.Failure(AppException(NetworkErrorType.getType(code)))
                }
            } catch (t: Throwable) {
                val type =
                    if (t is IOException || t is InterruptedIOException || t is UnknownHostException) NetworkErrorType.NoInternetConnection
                    else NetworkErrorType.Unexpected
                Result.Failure(AppException(type))
            } finally {
                urlConnection?.disconnect()
            }
        }
    }
}