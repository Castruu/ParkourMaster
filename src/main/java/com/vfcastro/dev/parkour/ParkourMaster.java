package com.vfcastro.dev.parkour;

import com.vfcastro.dev.parkour.config.DatabaseConfiguration;
import com.vfcastro.dev.parkour.database.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class ParkourMaster extends JavaPlugin {

    @Override
    public void onEnable() {
//        saveDefaultConfig();

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(
                "jdbc:mysql://localhost:3306/parkour_plugin",
                "root",
                "parkour-pw"
        );

        DatabaseManager databaseManager = new DatabaseManager(databaseConfiguration);
        try (Connection conn = databaseManager.getConnection()) {
            getLogger().info("Connected to MySQL via HikariCP!");
        } catch (SQLException e) {
            getLogger().severe("MySQL connection failed: " + e.getMessage());
        }

    }

    @Override
    public void onDisable() {
        // Plugin startup logic
    }
}
