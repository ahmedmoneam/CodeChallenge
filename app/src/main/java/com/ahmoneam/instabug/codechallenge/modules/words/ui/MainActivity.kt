package com.ahmoneam.instabug.codechallenge.modules.words.ui

import android.app.Activity
import android.os.Bundle
import android.widget.*
import com.ahmoneam.instabug.codechallenge.R
import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.ErrorType
import com.ahmoneam.instabug.core.error.NetworkErrorType
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
    private val containerView: RelativeLayout? get() = findViewById(R.id.containerFrameLayout)
    private val wordsListView: ListView? get() = findViewById(R.id.wordsListView)
    private val emptyView: LinearLayout? get() = findViewById(R.id.emptyErrorLinearLayout)
    private val retryButton: Button? get() = findViewById(R.id.retryButton)
    private val messageTextView: TextView? get() = findViewById(R.id.messageTextView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
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

    private fun initViews() {
        retryButton?.setOnClickListener { viewModel.getWords() }
    }

    private fun initListeners() {
        viewModel.addOnStatusUpdatedListener {
            when (it) {
                UiStatus.Idle -> hideLoading()
                UiStatus.Loading -> showLoading()
                UiStatus.Empty -> showEmpty()
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
        hideEmpty()
    }

    private fun showError(type: ErrorType, errorMessage: String?) {
        when (type) {
            NetworkErrorType.NoInternetConnection -> showEmpty(getString(R.string.message_no_internet_connection))
            else -> showEmpty(getString(R.string.message_unknown_error))
        }
    }

    private fun showEmpty(message: String? = null) {
        messageTextView?.text = message ?: getString(R.string.message_no_data_found)
        emptyView?.show()
        wordsListView?.hide()
    }

    private fun hideEmpty() {
        emptyView?.hide()
        wordsListView?.show()
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
