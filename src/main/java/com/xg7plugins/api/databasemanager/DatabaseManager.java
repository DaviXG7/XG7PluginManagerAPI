package com.xg7plugins.api.databasemanager;

import com.xg7plugins.api.XG7PluginManager;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    @Getter
    private static Connection connection;

    @SneakyThrows
    public static void connect(String host, int port, String database, String user, String pass) {


        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
        } catch (SQLException e) {

            Class.forName("org.sqlite.JDBC");

            File file = new File(XG7PluginManager.getPlugin().getDataFolder(), "data.db");

            if (!file.exists()) file.createNewFile();

            connection = DriverManager.getConnection("jdbc:sqlite:" + XG7PluginManager.getPlugin().getDataFolder().getPath() + "/playerdata.db");
        }
    }

    @SneakyThrows
    public static void closeConnection() {
        connection.close();
        connection = null;
    }

    private static String getSQLType(Class<?> clazz) {
        if (clazz == String.class) {
            return "TEXT";
        } else if (clazz == int.class || clazz == Integer.class) {
            return "INT";
        } else if (clazz == long.class || clazz == Long.class) {
            return "BIGINT";
        } else if (clazz == float.class || clazz == Float.class) {
            return "FLOAT";
        } else if (clazz == double.class || clazz == Double.class) {
            return "DOUBLE";
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return "BOOLEAN";
        } else if (clazz == char.class || clazz == Character.class) {
            return "CHAR";
        } else if (clazz == byte[].class) {
            return "BLOB";
        } else if (clazz == Timestamp.class) {
            return "TIMESTAMP";
        } else if (clazz == Date.class) {
            return "DATE";
        } else if (clazz == Time.class) {
            return "TIME";
        } else if (clazz == UUID.class) {
            return "VARCHAR(36)";
        }
        return "TEXT";
    }

    public static void createTableOf(Class<? extends Type> entity) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ").append(entity.getName().toLowerCase()).append(" (");
        Field[] fields = entity.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType().equals(List.class)) {
                createTableOf(field.getGenericType().getClass());
                return;
            }
            builder.append(field.getName()).append(" ").append(getSQLType(field.getType())).append(" ");
            if (field.getAnnotation(Entity.PKey.class) != null) {
                builder.append("PRIMARY KEY");
            }
            if (field.getAnnotation(Entity.FKey.class) != null) {
                builder.append("FOREIGN KEY");
            }
            if (i == fields.length - 1) break;
            builder.append(field.getName()).append(", ");
        }
        builder.append(");");

        executeUpdate(builder.toString());
    }
    @SneakyThrows
    public static <T> T selectClass(Class<?> clazz, Object id) {

        ResultSet set = executeQuery("SELECT * FROM " + clazz.getName().toLowerCase() + " WHERE id = ?;", id);

        List<Object> listFields = new ArrayList<>();

        for (Field field : clazz.getFields()) {
            if (field.getType().equals(List.class)) {
                T t = selectClass(field.getGenericType().getClass(), id);
                listFields.add(t);
                continue;
            }
            listFields.add(field.getName());
        }

        if (set.next()) {
            Object object = clazz.getConstructor().newInstance();
            for (int i = 0; i < listFields.size(); i++) {
                Field field = clazz.getField(listFields.get(i).toString());
                field.setAccessible(true);
                field.set(object, set.getObject(i + 1));
            }
            return (T) object;
        }
        return null;
    }

    @SneakyThrows
    public static ResultSet executeQuery(String sql, Object... args) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            return ps.executeQuery();
        }
    }


    @SneakyThrows
    public static int executeUpdate(String sql, Object... args) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            return ps.executeUpdate();
        }
    }

}
