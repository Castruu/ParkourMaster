package com.vfcastro.dev.parkour.database;

import java.sql.SQLException;

@FunctionalInterface
public interface DatabaseErrorCallback {

    void onDatabaseError(SQLException e);

}
