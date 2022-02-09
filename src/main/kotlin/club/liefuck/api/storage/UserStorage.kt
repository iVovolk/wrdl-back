package club.liefuck.api.storage

import club.liefuck.api.data.Users
import club.liefuck.api.data.dbQuery
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

object UserStorage {
    suspend fun setNew(user: User): Int = dbQuery {
        Users.insertAndGetId {
            it[uKey] = user.key
        }.value
    }

    suspend fun keyExists(key: String): Boolean = dbQuery {
        Users.slice(Users.uKey).select { Users.uKey.eq(key) }.singleOrNull() != null
    }

    suspend fun findIdByKey(key: String): Int? = dbQuery {
        Users.slice(Users.id).select { Users.uKey.eq(key) }.map { it[Users.id].value }.singleOrNull()
    }
}

data class User(val key: String)

