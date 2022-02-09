val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val mariadb_connector_version: String by project
val hikaricp_version: String by project
val flyway_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    id("org.flywaydb.flyway") version "8.4.3"
}

group = "club.liefuck.api"
version = "0.0.1"
application {
    mainClass.set("club.liefuck.api.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.mariadb.jdbc:mariadb-java-client:$mariadb_connector_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")

    implementation("org.flywaydb:flyway-core:$flyway_version")
    implementation("org.flywaydb:flyway-mysql:$flyway_version")
}

flyway {
    locations = arrayOf("filesystem:resources/db/migration")
}
