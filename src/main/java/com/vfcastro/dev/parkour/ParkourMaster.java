package com.vfcastro.dev.parkour;

import com.vfcastro.dev.parkour.commands.ParkourCommand;
import com.vfcastro.dev.parkour.config.Configuration;
import com.vfcastro.dev.parkour.config.DatabaseConfiguration;
import com.vfcastro.dev.parkour.database.DatabaseManager;
import com.vfcastro.dev.parkour.database.ParkourDatabaseManager;
import com.vfcastro.dev.parkour.events.TouchGoldenPlateEvent;
import com.vfcastro.dev.parkour.manager.ParkourManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class ParkourMaster extends JavaPlugin {

    private DatabaseManager databaseManager;
    private ParkourManager parkourManager;
    private Configuration configuration;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeConfiguration();
        initializeDatabase(configuration.databaseConfiguration());
        ParkourDatabaseManager parkourDatabaseManager = new ParkourDatabaseManager(this, databaseManager);
        parkourManager = new ParkourManager(this, parkourDatabaseManager);

        initializeCommands(parkourManager);
        initializeEventListeners();
    }

    @Override
    public void onDisable() {
        if(databaseManager != null) {
            databaseManager.close();
        }
    }

    private void initializeDatabase(DatabaseConfiguration databaseConfiguration) {
        databaseManager = new DatabaseManager(databaseConfiguration);

        if(databaseManager.initializeDatabase()) {
            getServer().sendPlainMessage("Database has been initialized!");
        } else {
            getLogger().severe("Database has not been initialized! Plugin will be disabled!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeCommands(ParkourManager parkourManager) {
        getServer().getCommandMap().register(
                "parkour",
                new ParkourCommand("parkour", "Command used to create and edit parkour",
                        """
                        - /parkour create <name> - Used to create a new parkour track with start at current location
                        - /parkour checkpoint <name> - Used to add a checkpoint to parkour at current location
                        - /parkour finish <name> - Set the last checkpoint as the finish line
                        - /parkour restart - Goes to the latest checkpoint
                        """,
                        parkourManager
                )
        );

        getServer().sendPlainMessage("Commands have been initialized!");
    }

    private void initializeConfiguration() {
        FileConfiguration config = getConfig();
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String username = config.getString("database.username");
        String password = config.getString("database.password");
        String database = config.getString("database.database");

        this.configuration = new Configuration(
                new DatabaseConfiguration(
                        String.format("jdbc:mysql://%s:%d/%s", host, port, database),
                        username,
                        password
                )
        );
    }

    private void initializeEventListeners() {
        getServer().getPluginManager().registerEvents(new TouchGoldenPlateEvent(parkourManager), this);
    }
}
