package com.vfcastro.dev.parkour.database;

@FunctionalInterface
public interface DatabaseResultCallback<T> {

    void onDatabaseConclusion(T result);
}
