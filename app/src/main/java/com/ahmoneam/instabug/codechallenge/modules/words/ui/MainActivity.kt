package com.ahmoneam.instabug.codechallenge.modules.words.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.ahmoneam.instabug.codechallenge.R
import com.ahmoneam.instabug.codechallenge.modules.words.entities.view.WordItemView
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.ErrorType
import com.ahmoneam.instabug.core.error.NetworkErrorType
import com.ahmoneam.instabug.core.remotedata.UiStatus
import com.ahmoneam.instabug.core.utils.hide
import com.ahmoneam.instabug.core.utils.show

class MainActivity : Activity() {
    companion object {
        private const val LISTVIEW_INTERNAL_STATE_KEY = "LISTVIEW_INTERNAL_STATE_KEY"
        private const val LISTVIEW_FIRST_VISIBLE_POSITION = "LISTVIEW_FIRST_VISIBLE_POSITION"
        private const val LISTVIEW_SORTING_ORDER = "LISTVIEW_SORTING_ORDER"
        private const val SORTING_ASCENDING = "SORTING_ASCENDING"
        private const val SORTING_DESCENDING = "SORTING_DESCENDING"
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
    private var sortingOrder: String? = null

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
            wordsListView?.smoothScrollToPosition(
                savedInstanceState.getInt(
                    LISTVIEW_FIRST_VISIBLE_POSITION
                )
            )
            sortingOrder = savedInstanceState.getString(LISTVIEW_SORTING_ORDER)
            toggleSort(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (wordsListView != null) {
            outState.putInt(
                LISTVIEW_FIRST_VISIBLE_POSITION,
                wordsListView?.firstVisiblePosition ?: 0
            )
            outState.putParcelable(
                LISTVIEW_INTERNAL_STATE_KEY,
                wordsListView?.onSaveInstanceState()
            )
            outState.putString(LISTVIEW_SORTING_ORDER, sortingOrder)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val myActionMenuItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView = myActionMenuItem.actionView as SearchView
        myActionMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?) = true

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                filter("")
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                filter(s)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                toggleSort()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRetainNonConfigurationInstance(): Any = viewModel

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

    private fun updateUi(data: List<WordItemView>) {
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

    private fun filter(query: String) {
        adapter.filter.filter(query)
    }

    private fun toggleSort(keepLatest: Boolean = false) {
        if (keepLatest && sortingOrder.isNullOrEmpty()) return
        if (keepLatest) {
            sortingOrder = when (sortingOrder) {
                SORTING_ASCENDING -> SORTING_DESCENDING
                else -> SORTING_ASCENDING
            }
        }
        when (sortingOrder) {
            null, SORTING_ASCENDING -> {
                sortingOrder = SORTING_DESCENDING
                adapter.sort { o1, o2 -> o1.text.compareTo(o2.text) }
            }
            else -> {
                sortingOrder = SORTING_ASCENDING
                adapter.sort { o1, o2 -> o2.text.compareTo(o1.text) }
            }
        }
    }
}
