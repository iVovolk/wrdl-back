package club.liefuck.api.service

import club.liefuck.api.storage.User
import club.liefuck.api.storage.UserStatsStorage
import club.liefuck.api.storage.UserStorage
import club.liefuck.api.util.randomString
import kotlinx.serialization.Serializable

object UserService {
    const val KEY_LENGTH = 20

    suspend fun addNew(): String {
        var userKey = randomString(KEY_LENGTH)
        while (UserStorage.keyExists(userKey)) {
            userKey = randomString()
        }
        UserStorage.setNew(User(userKey))
        return userKey
    }

    suspend fun findIdByKey(key: String): Int? = UserStorage.findIdByKey(key)

    suspend fun withStats(key: String): UserWithStats? {
        UserStorage.findIdByKey(key)?.let {
            with(UserStatsStorage.currentForUser(it)) {
                return UserWithStats(key, this.size, this.mode, this.lastId)
            }
        } ?: return null
    }
}

@Serializable
data class UserWithStats(val uKey: String, val size: Byte, val mode: Byte, val lastId: Int)
