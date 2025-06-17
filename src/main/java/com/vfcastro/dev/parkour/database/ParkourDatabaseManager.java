package com.vfcastro.dev.parkour.database;

import com.vfcastro.dev.parkour.entity.Checkpoint;
import com.vfcastro.dev.parkour.entity.Parkour;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParkourDatabaseManager {

    private final DatabaseManager databaseManager;
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public ParkourDatabaseManager(Plugin plugin, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    public void createParkour(Parkour parkour, DatabaseResultCallback<BigInteger> callback, DatabaseErrorCallback errorCallback) {
        scheduler.runTaskAsynchronously(
                plugin, () -> {
                    try (Connection connection = databaseManager.getConnection()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "INSERT INTO parkour(name, finished) VALUES (?, FALSE)",
                                Statement.RETURN_GENERATED_KEYS
                        )) {
                            statement.setString(1, parkour.name());

                            int affectedRows = statement.executeUpdate();
                            if (affectedRows > 0) {
                                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        callback.onDatabaseConclusion(BigInteger.valueOf(generatedKeys.getInt(1)));
                                        return;
                                    }
                                }
                            }
                            callback.onDatabaseConclusion(null);
                        }
                    } catch (SQLException e) {
                        errorCallback.onDatabaseError(e);
                    }
                }
        );
    }

    public void getParkour(BigInteger id, DatabaseResultCallback<Parkour> callback, DatabaseErrorCallback errorCallback) {
        scheduler.runTaskAsynchronously(
                plugin, () -> {
                    try (Connection connection = databaseManager.getConnection()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "SELECT * FROM parkour WHERE id = ? "
                        )) {
                            statement.setInt(1, id.intValue());
                            try (ResultSet resultSet = statement.executeQuery()) {
                                if (resultSet.next()) {
                                    callback.onDatabaseConclusion(new Parkour(
                                            BigInteger.valueOf(resultSet.getInt("id")),
                                            resultSet.getString("name"),
                                            resultSet.getBoolean("finished")
                                    ));
                                    return;
                                }
                                callback.onDatabaseConclusion(null);
                            }
                        }
                    } catch (SQLException e) {
                        errorCallback.onDatabaseError(e);
                    }
                }
        );
    }

    public void getAllParkour(DatabaseResultCallback<List<Parkour>> callback, DatabaseErrorCallback errorCallback) {
        scheduler.runTaskAsynchronously(
                plugin, () -> {
                    try (Connection connection = databaseManager.getConnection()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "SELECT * FROM parkour"
                        )) {
                            try (ResultSet resultSet = statement.executeQuery()) {
                                List<Parkour> parkourList = new ArrayList<>();
                                while (resultSet.next()) {
                                    parkourList.add(new Parkour(
                                            BigInteger.valueOf(resultSet.getInt("id")),
                                            resultSet.getString("name"),
                                            resultSet.getBoolean("finished")
                                    ));
                                }

                                callback.onDatabaseConclusion(parkourList);
                            }
                        }
                    } catch (SQLException e) {
                        errorCallback.onDatabaseError(e);
                    }
                }
        );
    }

    public void getCheckpointsFromParkour(Parkour parkour, DatabaseResultCallback<List<Checkpoint>> callback, DatabaseErrorCallback errorCallback) {
        scheduler.runTaskAsynchronously(
                plugin, () -> {
                    try (Connection connection = databaseManager.getConnection()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "SELECT * FROM parkour_checkpoint WHERE parkour_id = ?"
                        )) {
                            statement.setInt(1, parkour.id().intValue());

                            try (ResultSet resultSet = statement.executeQuery()) {
                                List<Checkpoint> checkpoints = new ArrayList<>();
                                while (resultSet.next()) {
                                    checkpoints.add(
                                            new Checkpoint(
                                                    parkour,
                                                    BigInteger.valueOf(resultSet.getInt("id")),
                                                    resultSet.getInt("step"),
                                                    resultSet.getInt("x"),
                                                    resultSet.getInt("y"),
                                                    resultSet.getInt("z")
                                            )
                                    );
                                }
                                callback.onDatabaseConclusion(checkpoints);
                            }

                        }
                    } catch (SQLException e) {
                        errorCallback.onDatabaseError(e);
                    }
                }
        );
    }

    public void finishParkour(Parkour parkour, DatabaseResultCallback<Boolean> callback, DatabaseErrorCallback errorCallback) {
        scheduler.runTaskAsynchronously(
                plugin, () -> {
                    try (Connection connection = databaseManager.getConnection()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "UPDATE parkour SET finished = TRUE WHERE id = ?"
                        )) {
                            statement.setInt(1, parkour.id().intValue());
                            callback.onDatabaseConclusion(statement.executeUpdate() > 0);
                        }
                    } catch (SQLException e) {
                        errorCallback.onDatabaseError(e);
                    }
                }
        );
    }


    public void addParkourCheckpointToParkour(Parkour parkour, Checkpoint checkpoint, DatabaseResultCallback<Boolean> callback, DatabaseErrorCallback errorCallback) {
        scheduler.runTaskAsynchronously(
                plugin, () -> {
                    try (Connection connection = databaseManager.getConnection()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "INSERT INTO parkour_checkpoint(parkour_id, step, x, y, z) VALUES (?, ?, ?, ?, ?)"
                        )) {
                            statement.setInt(1, parkour.id().intValue());
                            statement.setInt(2, checkpoint.step());
                            statement.setInt(3, checkpoint.x());
                            statement.setInt(4, checkpoint.y());
                            statement.setInt(5, checkpoint.z());

                            callback.onDatabaseConclusion(statement.executeUpdate() > 0);
                        }
                    } catch (SQLException e) {
                        errorCallback.onDatabaseError(e);
                    }
                }
        );
    }

    public void removeParkourCheckPointFromParkour(Checkpoint checkpoint, DatabaseResultCallback<Boolean> callback, DatabaseErrorCallback errorCallback) {
        scheduler.runTaskAsynchronously(
                plugin, () -> {
                    try (Connection connection = databaseManager.getConnection()) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "DELETE FROM parkour_checkpoint WHERE id = ?;"
                        )) {
                            statement.setInt(1, checkpoint.id().intValue());
                            callback.onDatabaseConclusion(statement.executeUpdate() > 0);
                        }
                    } catch (SQLException e) {
                        errorCallback.onDatabaseError(e);
                    }
                }
        );
    }


}
