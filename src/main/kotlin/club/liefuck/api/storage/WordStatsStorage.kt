package club.liefuck.api.storage

import club.liefuck.api.data.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import club.liefuck.api.data.WordStats as WSScheme

object WordStatsStorage {
    suspend fun getByWordId(wordId: Int): WordStats = dbQuery {
        WSScheme.select { WSScheme.wordId eq wordId }.map { it.toWordStats() }.singleOrNull() ?: WordStats()
    }

    suspend fun incFails(ws: WordStats, wordId: Int) = dbQuery {
        if (null == ws.id) {
            WSScheme.insert {
                it[this.wordId] = wordId
                it[shownTimes] = 1
                it[failTimes] = 1
                it[guessedTimes] = 0
            }
        } else {
            WSScheme.update({ WSScheme.id eq ws.id }) {
                it[shownTimes] = ws.shownTimes + 1
                it[failTimes] = ws.failTimes + 1
            }
        }
    }

    suspend fun incGuessed(ws: WordStats, wordId: Int) = dbQuery {
        if (null == ws.id) {
            WSScheme.insert {
                it[this.wordId] = wordId
                it[shownTimes] = 1
                it[failTimes] = 0
                it[guessedTimes] = 1
            }
        } else {
            WSScheme.update({ WSScheme.id eq ws.id }) {
                it[shownTimes] = ws.shownTimes + 1
                it[guessedTimes] = ws.guessedTimes + 1
            }
        }
    }
}

data class WordStats(
    val id: Int? = null,
    val shownTimes: Int = 0,
    val guessedTimes: Int = 0,
    val failTimes: Int = 0
)

private fun ResultRow.toWordStats(): WordStats = WordStats(
    this[WSScheme.id].value,
    this[WSScheme.shownTimes],
    this[WSScheme.guessedTimes],
    this[WSScheme.failTimes],
)
