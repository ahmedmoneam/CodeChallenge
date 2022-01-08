package com.ahmoneam.instabug.remote.di

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.remote.IRemoteDataSource
import com.ahmoneam.instabug.remote.RemoteDataSource
import com.ahmoneam.instabug.remote.parser.HtmlParser
import com.ahmoneam.instabug.remote.parser.IHtmlParser
import com.ahmoneam.instabug.remote.utils.ConnectivityUtils
import com.ahmoneam.instabug.remote.utils.IConnectivityUtils

object RemoteDI {
    fun init() {
        init(ConnectivityUtils(), RemoteDataSource(), HtmlParser())
    }

    fun init(
        connectivityUtils: IConnectivityUtils,
        remoteDataSource: IRemoteDataSource,
        htmlParser: IHtmlParser
    ) {
        initConnectivityUtils(connectivityUtils)
        initRemoteDataSource(remoteDataSource)
        initHtmlParser(htmlParser)
    }

    fun initConnectivityUtils(connectivityUtils: IConnectivityUtils) {
        SL.bindInstance(IConnectivityUtils::class.java, connectivityUtils)
    }

    fun initRemoteDataSource(remoteDataSource: IRemoteDataSource) {
        SL.bindInstance(IRemoteDataSource::class.java, remoteDataSource)
    }

    fun initHtmlParser(htmlParser: IHtmlParser) {
        SL.bindInstance(IHtmlParser::class.java, htmlParser)
    }
}