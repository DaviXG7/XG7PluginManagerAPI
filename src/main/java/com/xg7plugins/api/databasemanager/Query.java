package com.xg7plugins.api.databasemanager;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class Query {

    private Iterator<Map<String,Object>> results;

    public static Query create(String sql, Object... params) {
        return DBManager.executeQuery(sql,params);
    }

    public boolean hasNextLine() {
        return results.hasNext();
    }
    public Map<String, Object> nextLine() {
        return results.next();
    }

    public <T> T get(String key) {
        return (T) results.next().get(key);
    }

    @SneakyThrows
    public <T> T get(Class<T> clazz) {
        Map<String, Object> values = results.next();

        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            Object value = values.get(f.getName());

            if (value == null) continue;

            if (f.getType() == List.class) {
                ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
                Type tipoGenerico = parameterizedType.getActualTypeArguments()[0];

                List<Object> tList = new ArrayList<>();
                Object listInstance = ((Class<?>) tipoGenerico).getDeclaredConstructor().newInstance();

                for (Field fListf : ((Class<?>) tipoGenerico).getDeclaredFields()) {
                        fListf.setAccessible(true);
                        if (values.get(fListf.getName()) == null) continue;
                        fListf.set(listInstance, values.get(fListf.getName()));
                }
                tList.add(listInstance);
                tList.addAll(getResultList((Class<?>) tipoGenerico));

                f.set(instance, tList);

                continue;
            }
            f.set(instance, value);
        }
        return instance;
    }

    public <T> List<T> getResultList(Class<T> clazz) {
        List<T> tList = new ArrayList<>();
        while (results.hasNext()) {
            tList.add(get(clazz));
        }
        return tList;
    }
}

