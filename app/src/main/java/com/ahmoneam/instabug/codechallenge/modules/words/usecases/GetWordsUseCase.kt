package com.ahmoneam.instabug.codechallenge.modules.words.usecases

import com.ahmoneam.instabug.codechallenge.modules.words.data.IWordsRepository

class GetWordsUseCase(private val wordsRepository: IWordsRepository) {
    operator fun invoke() = wordsRepository.getWords()
}