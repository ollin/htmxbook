package com.nautsch.htmxbook;

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource


@Configuration
class DbConfig {

    @Primary
    @Bean
    fun dataSource(
    ): DataSource {
        return DataSourceProperties().apply {
            url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
        }.initializeDataSourceBuilder().build()
    }

    @Bean
    fun flyway(
        dataSource: DataSource,
    ): Flyway {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .cleanDisabled(false)
            .locations("classpath:db/migration")
            .load()

        flyway.migrate()

        return flyway
    }


    @Bean
    fun jooqDefaultConfigurationCustomizer(
        dataSource: DataSource,
    ): DefaultConfigurationCustomizer {
        return DefaultConfigurationCustomizer { configuration ->
            configuration.setDataSource(dataSource)
        }
    }
}
