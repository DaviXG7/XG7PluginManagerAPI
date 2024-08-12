package com.xg7plugins.api.databasemanager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xg7plugins.api.XG7PluginManager;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DBManager {

    @Getter
    private static Connection connection;

    @Getter
    private static Cache<Object, Entity> entitiesCached;

    @SneakyThrows
    public static void connect(String host, int port, String database, String user, String pass, ConnectionType type) {

        switch (type) {
            case MYSQL:
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
                break;
            case MARIADB:
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, user, pass);
                break;
            case SQLITE:
                Class.forName("org.sqlite.JDBC");
                File file = new File(XG7PluginManager.getPlugin().getDataFolder(), "data.db");

                if (!file.exists()) file.createNewFile();

                connection = DriverManager.getConnection("jdbc:sqlite:" + XG7PluginManager.getPlugin().getDataFolder().getPath() + "/data.db");
                break;
        }
    }

    public static void initCache(long cooldownToExpires, TimeUnit timeUnit) {
        entitiesCached = Caffeine.newBuilder().expireAfterWrite(cooldownToExpires, timeUnit).build();
    }

    @SneakyThrows
    public static void closeConnection() {
        connection.close();
        connection = null;
    }

    @SneakyThrows
    public static Query executeQuery(String sql, Object... args) {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);


        ResultSet rs = ps.executeQuery();

        List<Map<String, Object>> results = new ArrayList<>();

        while (rs.next()) {

            Map<String, Object> map = new HashMap<>();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) map.put(rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));

            results.add(map);
        }

        return new Query(results.iterator());
    }


    @SneakyThrows
    public static int executeUpdate(String sql, Object... args) {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
        return ps.executeUpdate();

    }


}
