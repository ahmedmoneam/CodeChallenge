package com.ahmoneam.instabug.codechallenge.modules.words.entities.view

data class WordItemView(val text: String, val count: Int) {
    override fun toString() = text
}