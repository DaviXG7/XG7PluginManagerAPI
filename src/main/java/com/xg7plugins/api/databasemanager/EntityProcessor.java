package com.xg7plugins.api.databasemanager;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

public class EntityProcessor {
    private static String getSQLType(Class<?> clazz) {
        if (clazz == String.class) return "TEXT";
        else if (clazz == int.class || clazz == Integer.class) return "INT(11)";
        else if (clazz == long.class || clazz == Long.class) return "BIGINT";
        else if (clazz == float.class || clazz == Float.class) return "FLOAT";
        else if (clazz == double.class || clazz == Double.class) return "DOUBLE";
        else if (clazz == boolean.class || clazz == Boolean.class) return "BOOLEAN";
        else if (clazz == char.class || clazz == Character.class) return "CHAR";
        else if (clazz == byte[].class) return "BLOB";
        else if (clazz == Timestamp.class) return "TIMESTAMP";
        else if (clazz == Date.class) return "DATE";
        else if (clazz == Time.class) return "TIME";
        else if (clazz == UUID.class) return "VARCHAR(36)";
        return "TEXT";
    }

    public static void createTableOf(Class<?> clazz) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS " + clazz.getSimpleName() + "(");
        Class<?> oneToManyClass = null;
        Field[] declaredFields = clazz.getDeclaredFields();
        List<String> fkeys = new ArrayList<>();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];

            if (field.isAnnotationPresent(Entity.PKey.class)) {
                builder.append(field.getName() + " " + getSQLType(field.getType()) + " PRIMARY KEY");
                if (field.getAnnotation(Entity.PKey.class).autoincrement()) builder.append(" AUTO_INCREMENT");

                if (i == declaredFields.length - 1) break;

                builder.append(", ");
                continue;
            }
            if (field.isAnnotationPresent(Entity.FKey.class)) {
                Entity.FKey fKey = field.getAnnotation(Entity.FKey.class);

                fkeys.add("FOREIGN KEY (" + field.getName() + ") REFERENCES " + fKey.table() + "(" + fKey.reference() + ")");

            }
            if (field.getType().equals(List.class)) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type tipoGenerico = parameterizedType.getActualTypeArguments()[0];

                oneToManyClass = (Class<?>) tipoGenerico;

                if (i == declaredFields.length - 1) {
                    builder.replace(builder.length() - 2, builder.length(), "");
                    break;
                }

                continue;
            }

            builder.append(field.getName() + " " + getSQLType(field.getType()) + " NOT NULL");

            if (i == declaredFields.length - 1) break;

            builder.append(", ");

        }

        fkeys.forEach(fkey -> builder.append(", ").append(fkey));
        builder.append(");");
        DBManager.executeUpdate(builder.toString());
        if (oneToManyClass != null) createTableOf(oneToManyClass);

    }

    @SneakyThrows
    public static void insetEntity(Class<?> entityClass, Object entity) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO " + entityClass.getSimpleName() + " VALUES (");

        Arrays.stream(entityClass.getDeclaredFields()).filter(field -> !field.getType().equals(List.class)).map(field -> "?,").forEach(builder::append);
        builder.replace(builder.length() - 1, builder.length(), ")");

        List<Object> args = new ArrayList<>();
        List<Object> childs = new ArrayList<>();

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(List.class)) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type tipoGenerico = parameterizedType.getActualTypeArguments()[0];

                List<?> list = (List<?>) field.get(entity);
                childs.addAll(list);

                continue;
            }
            args.add(field.get(entity));
        }
        DBManager.executeUpdate(builder.toString(),args.toArray());
        if (!childs.isEmpty()) childs.forEach(item -> insetEntity(item.getClass(), item));
    }

}
