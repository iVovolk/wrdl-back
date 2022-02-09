package club.liefuck.api.data

import org.jetbrains.exposed.dao.id.IntIdTable

object Words : IntIdTable() {
    val word = varchar("word", 7, UTF8_COLLATION).uniqueIndex("idx_words_unique_word")
    val size = byte("size")
    val mode = byte("mode")
}


object WordStats : IntIdTable() {
    val wordId = integer("word_id").references(Words.id)
    val shownTimes = integer("shown_times").default(0)
    val guessedTimes = integer("guessed_times").default(0)
    val failTimes = integer("fail_times").default(0)
}

object Users : IntIdTable() {
    val uKey = varchar("u_key", 20, UTF8_COLLATION)
}

object UserStats : IntIdTable() {
    val userId = integer("user_id").references(Users.id)
    val size = byte("size")
    val mode = byte("mode")
    val failTimes = integer("fail_times").default(0)
    val successTimes = integer("success_times").default(0)
    val isCurrent = byte("is_current").default(0)
    val lastWordId = integer("last_word_id")

    init {
        uniqueIndex("idx_user_config_set", userId, size, mode)
    }
}
