package club.liefuck.api.storage

import club.liefuck.api.data.Words
import club.liefuck.api.data.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

object WordStorage {
    suspend fun current(lastId: Int, mode: Byte, size: Byte): Word? = dbQuery {
        Words.slice(Words.word, Words.id)
            .select { Words.id.greater(lastId) and (Words.mode eq mode) and (Words.size eq size) }
            .orderBy(Words.id, SortOrder.ASC)
            .limit(1)
            .map { it.toWord() }
            .singleOrNull()
    }

    suspend fun exists(word: String): Boolean = dbQuery {
        Words.slice(Words.id).select { Words.word eq word }.singleOrNull() != null
    }
}

data class Word(val id: Int, val word: String)

private fun ResultRow.toWord(): Word = Word(this[Words.id].value, this[Words.word])

enum class Mode(val mode: Byte) {
    Easy(1),
    Normal(2),
    Painful(3)
}

enum class Size(val size: Byte) {
    Four(4),
    Five(5)
}
