package org.example.infra.database

import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseConnection {
    fun init() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/microservices",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )
    }
}