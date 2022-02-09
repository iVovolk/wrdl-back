package club.liefuck.api.data

import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

const val UTF8_COLLATION = "utf8mb4_unicode_520_ci"

object DatabaseFactory {

    fun init(config: HoconApplicationConfig) {
        val dbUrl = config.property("db.jdbcUrl").getString()
        val dbUser = config.property("db.user").getString()
        val dbPassword = config.property("db.password").getString()
        val ds = hikari(dbUrl, dbUser, dbPassword)
        Database.connect(ds)
        val flyway = Flyway
            .configure()
            .dataSource(ds)
            .load()
        flyway.migrate()
    }

    private fun hikari(dbUrl: String, dbUser: String, dbPassword: String): HikariDataSource {
        val ds = HikariDataSource();
        ds.jdbcUrl = dbUrl
        ds.username = dbUser
        ds.password = dbPassword
        ds.transactionIsolation = "TRANSACTION_READ_COMMITTED"
        ds.maximumPoolSize = 3
        ds.isAutoCommit = false
        return ds
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
