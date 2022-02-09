package club.liefuck.api.service

import club.liefuck.api.storage.UserStats
import club.liefuck.api.storage.UserStatsStorage
import club.liefuck.api.storage.WordStatsStorage
import club.liefuck.api.storage.isCurrent
import kotlinx.serialization.Serializable

object StatsService {
    suspend fun handleFail(config: Config, wordId: Int) {
        with(WordStatsStorage.getByWordId(wordId)) {
            WordStatsStorage.incFails(this, wordId)
        }
        with(UserStatsStorage.getByConfig(config)) {
            if (!isCurrent()) {
                UserStatsStorage.dropCurrentForUser(config.userId)
            }
            UserStatsStorage.incFails(this, config.userId, wordId)
        }
    }

    suspend fun handleSuccess(config: Config, wordId: Int) {
        with(WordStatsStorage.getByWordId(wordId)) {
            WordStatsStorage.incGuessed(this, wordId)
        }
        with(UserStatsStorage.getByConfig(config)) {
            if (!isCurrent()) {
                UserStatsStorage.dropCurrentForUser(config.userId)
            }
            UserStatsStorage.incSuccess(this, config.userId, wordId)
        }
    }

    suspend fun currentForUser(config: Config): UserStats = UserStatsStorage.currentByConfig(config)

    suspend fun load(config: Config): RoundStats {
        val us = UserStatsStorage.countForUser(config.userId)
        val ws = with(UserStatsStorage.currentByConfig(config)) {
            WordStatsStorage.getByWordId(this.lastId)
        }
        return RoundStats(
            us?.fails ?: 0,
            us?.successes ?: 0,
            us?.total ?: 0,
            ws.failTimes,
            ws.guessedTimes,
            ws.shownTimes
        )
    }
}

@Serializable
data class RoundStats(
    val userFails: Int,
    val userSuccesses: Int,
    val userTotal: Int,
    val wordFails: Int,
    val wordSuccesses: Int,
    val wordTotal: Int
)

data class Config(val userId: Int, val size: Byte, val mode: Byte)
