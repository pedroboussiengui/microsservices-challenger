package org.example.infra.database

import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseConnection {
    fun init(url: String, user: String, password: String) {
        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )
    }
}