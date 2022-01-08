package com.ahmoneam.instabug.codechallenge.modules.words

import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word

fun Map<String, Int>.mapToWords() = map { Word(it.key, it.value) }