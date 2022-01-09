package com.ahmoneam.instabug.codechallenge.modules.words.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.ahmoneam.instabug.codechallenge.R
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.remotedata.UiStatus
import com.ahmoneam.instabug.core.utils.IdlingResource
import com.ahmoneam.instabug.core.utils.test.IAndroidTestIdlingResource
import com.ahmoneam.instabug.local.module.word.cache.IWordCacheManager
import com.ahmoneam.instabug.remote.RemoteDataSource
import com.ahmoneam.instabug.remote.di.RemoteDI
import com.ahmoneam.instabug.remote.parser.HtmlParser
import com.ahmoneam.instabug.remote.utils.ConnectivityUtils
import com.ahmoneam.instabug.remote.utils.IConnectivityUtils
import io.mockk.every
import io.mockk.spyk
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var connectivityUtils: IConnectivityUtils
    private lateinit var wordCacheManager: IWordCacheManager

    private fun runTest(
        enableIdling: Boolean = false,
        preLunch: () -> Unit,
        test: (ActivityScenario<MainActivity>) -> Unit
    ) {
        val idlingResource = SL[IAndroidTestIdlingResource::class.java]
        if (enableIdling) {
            IdlingRegistry.getInstance().register(idlingResource.countingIdlingResource)
        }
        preLunch()
        scenario = launchActivity()
        test(scenario)
        if (enableIdling)
            IdlingRegistry.getInstance().unregister(idlingResource.countingIdlingResource)
    }

    @Before
    fun setUp() {
        SL.bindInstance(IAndroidTestIdlingResource::class.java, IdlingResource)
        SL.bindInstance(
            IWordCacheManager::class.java,
            spyk(SL[IWordCacheManager::class.java]).also { wordCacheManager = it })
        RemoteDI.init(
            spyk(ConnectivityUtils()).also { connectivityUtils = it },
            spyk(RemoteDataSource()),
            spyk(HtmlParser())
        )
    }

    @Test
    fun test_Main_Activity_Loading() {
        runTest(
            preLunch = {},
            test = {
                SL[WordsListViewModel::class.java].addOnStatusUpdatedListener {
                    if (it is UiStatus.Loading) {
                        onView(withId(R.id.loadingProgressBar))
                            .check(matches(isDisplayed()))
                    }
                }
            })
    }

    @Test
    fun test_Main_Activity_With_No_Internet_Connection_And_Empty_Cache() {
        runTest(
            preLunch = {
                every { wordCacheManager.isWordsCacheEmpty() } returns true
                every { connectivityUtils.isNetworkConnected } returns false
            },
            test = {
                val onView = onView(withId(R.id.messageTextView))
                onView.check(matches(isDisplayed()))
                    .check(matches(withText(R.string.message_no_internet_connection)))
            })
    }

    @Test
    fun test_Main_Activity_With_No_Internet_Connection_And_Corrupted_Cache() {
        runTest(
            preLunch = {
                every { connectivityUtils.isNetworkConnected } returns false
                every { wordCacheManager.isWordsCacheEmpty() } returns false
                every { wordCacheManager.getWordsCache() } answers { throw RuntimeException() }
            },
            test = {
                onView(withId(R.id.messageTextView))
                    .check(matches(isDisplayed()))
                    .check(matches(withText(R.string.message_unknown_error)))
            })
    }

    @Test
    fun test_Main_Activity_Sort_Ascending() {
        runTest(enableIdling = true, {}) {
            onView(
                allOf(
                    withId(R.id.action_sort),
                    isDisplayed()
                )
            ).perform(click())

            onView(withId(R.id.wordsListView))
                .check(matches(isDisplayed()))

            onData(anything())
                .inAdapterView(withId(R.id.wordsListView))
                .atPosition(0)
                .onChildView(withId(R.id.wordTextView))
                .check(matches(withText("A")))
        }
    }

    @Test
    fun test_Main_Activity_Toggle_Sort_Ascending_Then_Descending() {
        runTest(enableIdling = true, {}) {
            val sortView = onView(
                allOf(
                    withId(R.id.action_sort),
                    isDisplayed()
                )
            )
            onView(withId(R.id.wordsListView))
                .check(matches(isDisplayed()))

            sortView.perform(click())

            // assert that ascending at first
            onData(anything())
                .inAdapterView(withId(R.id.wordsListView))
                .atPosition(0)
                .onChildView(withId(R.id.wordTextView))
                .check(matches(withText("A")))

            // click sort again
            sortView.perform(click())

            // assert that toggle to descending at first
            onData(anything())
                .inAdapterView(withId(R.id.wordsListView))
                .atPosition(0)
                .onChildView(withId(R.id.wordTextView))
                .check(matches(withText("ZENDESK")))
        }
    }

    @Test
    fun test_Main_Activity_Search() {
        runTest(enableIdling = true, {}) {
            onView(
                allOf(
                    withId(R.id.action_search),
                    isDisplayed()
                )
            ).perform(click())

            onView(
                allOf(
                    withClassName(`is`("android.widget.SearchView\$SearchAutoComplete")),
                    isDisplayed()
                )
            )
                .perform(click())
                .perform(
                    replaceText("and"),
                    closeSoftKeyboard()
                )

            onData(anything())
                .inAdapterView(withId(R.id.wordsListView))
                .atPosition(0)
                .onChildView(withId(R.id.wordTextView))
                .check(matches(withText("ANDROID")))

            onData(anything())
                .inAdapterView(withId(R.id.wordsListView))
                .atPosition(1)
                .onChildView(withId(R.id.wordTextView))
                .check(matches(withText("AND")))
        }
    }

    @After
    fun tearDown() {
        if (::scenario.isInitialized) scenario.close()
    }
}
