package com.vfcastro.dev.parkour.config;

public class DatabaseConfiguration {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfiguration(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
