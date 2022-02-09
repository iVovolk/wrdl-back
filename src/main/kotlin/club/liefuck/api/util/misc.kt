package club.liefuck.api.util

import kotlin.random.Random

fun randomString(len: Int = 20): String {
    val charPool: List<Char> = ('A'..'Z') + ('0'..'9') + ('a'..'z')
    return (1..len)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}
