package com.ahmoneam.instabug.remote

import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.core.threading.CustomFutureTask
import com.ahmoneam.instabug.remote.utils.IConnectivityUtils

interface IRemoteDataSource {
    val connectivityUtils: IConnectivityUtils
    fun getHtmlPage(url: String): CustomFutureTask<Result<String>>
}