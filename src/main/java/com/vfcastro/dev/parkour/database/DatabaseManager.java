package com.vfcastro.dev.parkour.database;

import com.vfcastro.dev.parkour.config.DatabaseConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager implements AutoCloseable {

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

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean initializeDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            Statement sql = connection.createStatement();
            sql.execute(
                    "CREATE TABLE IF NOT EXISTS parkour(" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL UNIQUE," +
                            "finished BOOLEAN NOT NULL" +
                            ");"
            );
            sql.execute(
                    "CREATE TABLE IF NOT EXISTS parkour_checkpoint(" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "parkour_id BIGINT NOT NULL, " +
                            "step INTEGER NOT NULL, " +
                            "x INTEGER NOT NULL, " +
                            "y INTEGER NOT NULL, " +
                            "z INTEGER NOT NULL, " +
                            "FOREIGN KEY (parkour_id) REFERENCES parkour(id) ON DELETE CASCADE," +
                            "CONSTRAINT unique_parkour_step UNIQUE (parkour_id, step)," +
                            "CONSTRAINT unique_parkour_location UNIQUE (x, y, z)" +
                            ");"
            );
            sql.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


}
