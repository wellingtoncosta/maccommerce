package br.com.maccommerce.inventoryservice.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static br.com.maccommerce.inventoryservice.app.util.Constants.RESOURCES_BASE_PACKAGE;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = { RESOURCES_BASE_PACKAGE })
@EnableJpaRepositories(basePackages = { RESOURCES_BASE_PACKAGE })
public class DatabaseConfig {

    public @Bean @FlywayDataSource DataSource provideDataSource(DataSourceProperties dataSourceProperties) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(dataSourceProperties.getDriverClassName());
        config.setJdbcUrl(dataSourceProperties.getUrl());
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());

        return new HikariDataSource(config);
    }

}
