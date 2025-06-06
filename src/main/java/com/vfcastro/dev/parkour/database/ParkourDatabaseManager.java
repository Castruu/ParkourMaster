package com.vfcastro.dev.parkour.database;

import com.vfcastro.dev.parkour.data.ParkourData;
import com.vfcastro.dev.parkour.entity.Checkpoint;
import com.vfcastro.dev.parkour.entity.Parkour;

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

    public ParkourDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public BigInteger createParkour(Parkour parkour) {
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
                            return BigInteger.valueOf(generatedKeys.getInt(1));
                        }
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create parkour", e);
        }
    }

    public Parkour getParkour(BigInteger id) {
        try (Connection connection = databaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM parkour WHERE id = ? "
            )) {
                statement.setInt(1, id.intValue());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next()) {
                        return new Parkour(
                                BigInteger.valueOf(resultSet.getInt("id")),
                                resultSet.getString("name"),
                                resultSet.getBoolean("finished")
                        );
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not get parkour " + id, e);
        }
    }

    public List<Parkour> getAllParkour() {
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

                    return parkourList;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not get list of parkour", e);
        }
    }

    public List<Checkpoint> getCheckpointsFromParkour(Parkour parkour) {
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
                    return checkpoints;
                }

            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not get list of checkpoints", e);
        }
    }

    public boolean deleteParkour(Parkour parkour) {
        try (Connection connection = databaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM parkour WHERE id = ?;"
            )) {
                statement.setInt(1, parkour.id().intValue());
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not delete parkour " + parkour, e);
        }
    }

    public boolean finishParkour(Parkour parkour) {
        try (Connection connection = databaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE parkour SET finished = TRUE WHERE id = ?"
            )) {
                statement.setInt(1, parkour.id().intValue());
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not finish parkour " + parkour, e);
        }
    }


    public boolean addParkourCheckpointToParkour(Parkour parkour, Checkpoint checkpoint) {
        try (Connection connection = databaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO parkour_checkpoint(parkour_id, step, x, y, z) VALUES (?, ?, ?, ?, ?)"
            )) {
                statement.setInt(1, parkour.id().intValue());
                statement.setInt(2, checkpoint.step());
                statement.setInt(3, checkpoint.x());
                statement.setInt(4, checkpoint.y());
                statement.setInt(5, checkpoint.z());

                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not add checkpoint to parkour " + parkour, e);
        }
    }

    public boolean removeParkourCheckPointFromParkour(Checkpoint checkpoint) {
        try (Connection connection = databaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM parkour_checkpoint WHERE id = ?;"
            )) {
                statement.setInt(1, checkpoint.id().intValue());

                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not remove checkpoint " + checkpoint, e);
        }
    }


}
