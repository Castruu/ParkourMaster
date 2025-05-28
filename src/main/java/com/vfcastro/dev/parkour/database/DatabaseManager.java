package com.vfcastro.dev.parkour.database;

import com.vfcastro.dev.parkour.config.DatabaseConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    public final HikariDataSource dataSource;

    public DatabaseManager(DatabaseConfiguration configuration) {
        this.dataSource = connect(configuration);
    }

    public HikariDataSource connect(DatabaseConfiguration configuration) {
        HikariConfig config = new HikariConfig();
        config.setPassword(configuration.password());
        config.setJdbcUrl(configuration.url());
        config.setUsername(configuration.username());
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
