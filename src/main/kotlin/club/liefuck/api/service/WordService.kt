package club.liefuck.api.service

import club.liefuck.api.plugins.WordCheckRequest
import club.liefuck.api.storage.WordStorage
import kotlinx.serialization.Serializable

object WordService {
    suspend fun check(wr: WordCheckRequest, userId: Int, lastTurn: Boolean = false): WordResponse? {
        if (!WordStorage.exists(wr.word)) {
            return null
        }
        val config = Config(userId, wr.size, wr.mode)
        val us = StatsService.currentForUser(config)
        val currentWord = WordStorage.current(us.lastId, wr.mode, wr.size) ?: run {
            throw IllegalStateException("Слова такой длины и сложности закончились, может попробуем что-то еще?")
        }
        val solution = currentWord.word.toCharArray()
        val guess = wr.word.toCharArray()
        val res = mutableMapOf<Int, Int>()
        solution.forEachIndexed { i, c ->
            if (guess[i] == c) {
                res[i] = 1
                solution[i] = '-'
                guess[i] = '_'
            }
        }
        val success = wr.size.toInt() == res.size
        if (success) {
            StatsService.handleSuccess(config, currentWord.id)
            return WordResponse(res, true, currentWord.id, null)
        }

        guess.forEachIndexed { i, c ->
            if (c == '_') {
                return@forEachIndexed
            }
            val si = solution.indexOf(c)
            if (si != -1) {
                res[i] = 2
                solution[si] = '-'
                return@forEachIndexed
            }
            res[i] = 0
        }

        if (lastTurn) {
            StatsService.handleFail(Config(userId, wr.size, wr.mode), currentWord.id)
            return WordResponse(res, false, currentWord.id, currentWord.word)
        }
        return WordResponse(res, success, currentWord.id, null)
    }
}

@Serializable
data class WordResponse(val highlights: Map<Int, Int>, val success: Boolean, val lastId: Int, val solution: String?)
