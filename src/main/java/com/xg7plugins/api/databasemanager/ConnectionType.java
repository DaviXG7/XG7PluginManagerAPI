package com.xg7plugins.api.databasemanager;

import lombok.Getter;

@Getter
public enum ConnectionType {

    SQLITE("sqlite"),
    MYSQL("mysql"),
    MARIADB("postgresql");

    private final String name;

    ConnectionType(String name) {
        this.name = name;
    }

}
