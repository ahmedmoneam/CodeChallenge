package com.ahmoneam.instabug.codechallenge.modules.words.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ahmoneam.instabug.codechallenge.R
import com.ahmoneam.instabug.codechallenge.modules.words.entities.view.WordItemView

class WordsAdapter(context: Context, objects: MutableList<WordItemView>) :
    ArrayAdapter<WordItemView>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return (convertView
            ?: LayoutInflater.from(context)
                .inflate(R.layout.item_word, parent, false))
            .apply { bind(this, getItem(position)) }
    }

    private fun bind(view: View, word: WordItemView?) {
        word?.let {
            view.findViewById<TextView>(R.id.wordTextView)?.text = it.text
            view.findViewById<TextView>(R.id.wordCountTextView)?.text = it.count.toString()
        }
    }
}