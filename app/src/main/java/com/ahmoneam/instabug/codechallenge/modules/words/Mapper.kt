package com.ahmoneam.instabug.codechallenge.modules.words

import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word
import com.ahmoneam.instabug.local.module.word.entities.WordCache

fun Map<String, Int>.mapToWords() = map { Word(it.key, it.value) }

fun List<WordCache>.mapToWords() = map { Word(it.text, it.count) }

fun List<Word>.mapToCacheWord() = map { WordCache(it.text, it.count) }
