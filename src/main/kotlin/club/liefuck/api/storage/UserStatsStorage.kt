package club.liefuck.api.storage

import club.liefuck.api.data.dbQuery
import club.liefuck.api.service.Config
import org.jetbrains.exposed.sql.*
import club.liefuck.api.data.UserStats as USScheme

object UserStatsStorage {
    suspend fun currentForUser(userId: Int): UserStats = dbQuery {
        USScheme.select { USScheme.userId eq userId and (USScheme.isCurrent eq 1) }
            .map { it.toStats() }
            .singleOrNull()
            ?: UserStats()
    }

    suspend fun currentByConfig(config: Config): UserStats = dbQuery {
        USScheme.select {
            USScheme.userId eq config.userId and (
                    USScheme.size eq config.size
                    ) and (
                    USScheme.mode eq config.mode
                    ) and (
                    USScheme.isCurrent eq 1
                    )
        }
            .map { it.toStats() }
            .singleOrNull()
            ?: UserStats(size = config.size, mode = config.mode)
    }

    suspend fun getByConfig(config: Config): UserStats = dbQuery {
        USScheme.select { USScheme.userId eq config.userId and (USScheme.size eq config.size) and (USScheme.mode eq config.mode) }
            .map { it.toStats() }
            .singleOrNull()
            ?: UserStats(size = config.size, mode = config.mode)
    }

    suspend fun dropCurrentForUser(userId: Int) = dbQuery {
        USScheme.update({ USScheme.userId eq userId }) {
            it[isCurrent] = 0
        }
    }

    suspend fun incFails(us: UserStats, userId: Int, wordId: Int) = dbQuery {
        if (us.id == null) {
            USScheme.insert {
                it[this.userId] = userId
                it[size] = us.size
                it[mode] = us.mode
                it[lastWordId] = wordId
                it[failTimes] = 1
                it[successTimes] = 0
                it[isCurrent] = 1
            }
        } else {
            USScheme.update({ USScheme.id eq us.id }) {
                it[lastWordId] = wordId
                it[failTimes] = us.failTimes + 1
                it[isCurrent] = 1
            }
        }
    }

    suspend fun incSuccess(us: UserStats, userId: Int, wordId: Int) = dbQuery {
        if (us.id == null) {
            USScheme.insert {
                it[this.userId] = userId
                it[size] = us.size
                it[mode] = us.mode
                it[lastWordId] = wordId
                it[failTimes] = 0
                it[successTimes] = 1
                it[isCurrent] = 1
            }
        } else {
            USScheme.update({ USScheme.id eq us.id }) {
                it[lastWordId] = wordId
                it[successTimes] = us.successTimes + 1
                it[isCurrent] = 1
            }
        }
    }

    suspend fun countForUser(userId: Int): UserTotal? = dbQuery {
        USScheme.slice(USScheme.successTimes.sum(), USScheme.failTimes.sum(), USScheme.userId)
            .select { USScheme.userId eq userId }
            .map { it.toUserTotal() }
            .singleOrNull()
    }
}

data class UserTotal(val id: Int, val successes: Int, val fails: Int) {
    val total: Int
        get() = successes + fails
}

private fun ResultRow.toUserTotal() = UserTotal(
    this[USScheme.userId],
    this[USScheme.successTimes.sum()] ?: 0,
    this[USScheme.failTimes.sum()] ?: 0
)

data class UserStats(
    val id: Int? = null,
    val size: Byte = Size.Four.size,
    val mode: Byte = Mode.Normal.mode,
    val lastId: Int = 0,
    val failTimes: Int = 0,
    val successTimes: Int = 0,
    val isCurrent: Byte = 0,
)

fun UserStats.isCurrent(): Boolean = isCurrent.toInt() == 1

private fun ResultRow.toStats(): UserStats = UserStats(
    id = this[USScheme.id].value,
    size = this[USScheme.size],
    mode = this[USScheme.mode],
    lastId = this[USScheme.lastWordId],
    failTimes = this[USScheme.failTimes],
    successTimes = this[USScheme.successTimes],
    isCurrent = this[USScheme.isCurrent],
)
