package com.ahmoneam.instabug.remote

import com.ahmoneam.instabug.core.di.CoreDI
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.NetworkErrorType
import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.remote.di.RemoteDI
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

class RemoteDataSourceTest {

    private lateinit var remoteDataSource: IRemoteDataSource

    @Before
    fun setUp() {
        CoreDI.initIdlingResource()
        CoreDI.init(Executors.newFixedThreadPool(1))
        RemoteDI.init(mockk(), spyk<RemoteDataSource>(), mockk())
        remoteDataSource = SL[IRemoteDataSource::class.java]
    }

    @Test
    fun `test network call with no internet connection`() {
        every { remoteDataSource.connectivityUtils.isNetworkConnected } answers { false }

        val result = remoteDataSource.getHtmlPage("").get()
        assertThat(result, instanceOf(Result.Failure::class.java))
        assertThat((result as Result.Failure).type, `is`(NetworkErrorType.NoInternetConnection))
    }

    @Test
    fun `test real network call and check the html response`() {
        every { remoteDataSource.connectivityUtils.isNetworkConnected } answers { true }

        val result = remoteDataSource.getHtmlPage("https://instabug.com").get()
        assertThat(result, instanceOf(Result.Success::class.java))
        assertThat((result as Result.Success<String>).value, containsString("<body>"))
        assertThat(result.value, containsString("instabug"))
    }
}