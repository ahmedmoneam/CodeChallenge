package com.ahmoneam.instabug.codechallenge.modules.words.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.ProgressBar
import com.ahmoneam.instabug.codechallenge.R
import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.ErrorType
import com.ahmoneam.instabug.core.remotedata.UiStatus
import com.ahmoneam.instabug.core.utils.hide
import com.ahmoneam.instabug.core.utils.show

class MainActivity : Activity() {
    companion object {
        private const val LISTVIEW_INTERNAL_STATE_KEY = "LISTVIEW_INTERNAL_STATE_KEY"
    }

    private val viewModel: WordsListViewModel
        get() {
            return if (lastNonConfigurationInstance is WordsListViewModel)
                lastNonConfigurationInstance as WordsListViewModel
            else SL[WordsListViewModel::class.java]
        }

    private lateinit var adapter: WordsAdapter

    private val loadingView: ProgressBar? get() = findViewById(R.id.loadingProgressBar)
    private val containerView: FrameLayout? get() = findViewById(R.id.containerFrameLayout)
    private val wordsListView: ListView? get() = findViewById(R.id.wordsListView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListeners()
        if (savedInstanceState != null) {
            viewModel.onRestoreState()
            wordsListView?.onRestoreInstanceState(
                savedInstanceState.getParcelable(
                    LISTVIEW_INTERNAL_STATE_KEY
                )
            )
        }
    }

    override fun onRetainNonConfigurationInstance(): Any = viewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (wordsListView != null) {
            outState.putParcelable(
                LISTVIEW_INTERNAL_STATE_KEY,
                wordsListView?.onSaveInstanceState()
            )
        }
    }

    override fun onDestroy() {
        viewModel.destroy()
        super.onDestroy()
    }

    private fun initListeners() {
        viewModel.addOnStatusUpdatedListener {
            when (it) {
                UiStatus.Idle -> hideLoading()
                UiStatus.Loading -> showLoading()
                is UiStatus.Success -> updateUi(it.data)
                is UiStatus.Failure -> showError(it.type, it.errorMessage)
            }
        }
    }

    private fun updateUi(data: List<Word>) {
        if (!::adapter.isInitialized) {
            adapter = WordsAdapter(this, data.toMutableList())
            wordsListView?.adapter = adapter
        } else {
            adapter.clear()
            adapter.addAll(data)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showError(type: ErrorType, errorMessage: String?) {
        Log.v("MainActivity", "Error...$type and message->$errorMessage")
//        findViewById<TextView>(R.id.text).text = type.toString()
    }

    private fun showLoading() {
        containerView?.hide()
        loadingView?.show()
    }

    private fun hideLoading() {
        containerView?.show()
        loadingView?.hide()
    }
}
