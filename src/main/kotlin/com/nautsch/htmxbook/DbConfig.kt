package com.nautsch.htmxbook;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource


@Configuration
public class DbConfig {

    @Bean
    fun dataSource(): DataSource {
        return DataSourceProperties().apply {
            url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
        }.initializeDataSourceBuilder().build()
    }
}
