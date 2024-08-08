package com.xg7plugins.api;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config {

    @Getter
    private static final HashMap<String, FileConfiguration> configs = new HashMap<>();

    public static void init(String... filePaths) {

        for (String filePath : filePaths) {
            filePath += ".yml";
            File config = new File(XG7PluginManager.getPlugin().getDataFolder(), filePath);

            if (!config.exists()) XG7PluginManager.getPlugin().saveResource(filePath, false);

            configs.put(filePath, YamlConfiguration.loadConfiguration(config));
        }

    }

    public static FileConfiguration getConfig(String name) {
        return configs.get(name + ".yml");
    }
    public static Object get(String name, String path) {
        return configs.get(name + ".yml").get(path);
    }

    public static int getInt(String name, String path) {
        return configs.get(name + ".yml").getInt(path);
    }

    public static String getString(String name, String path) {
        return configs.get(name + ".yml").getString(path);
    }

    public static boolean getBoolean(String name, String path) {
        return configs.get(name + ".yml").getBoolean(path);
    }

    public static long getLong(String name, String path) {
        return configs.get(name + ".yml").getLong(path);
    }

    public static double getDouble(String name, String path) {
        return configs.get(name + ".yml").getDouble(path);
    }

    public static List<String> getList(String name, String path) {
        return configs.get(name + ".yml").getStringList(path);
    }
    public static List<Integer> getIntegerList(String name, String path) {
        return configs.get(name + ".yml").getIntegerList(path);
    }

    public static Set<String> getConfigurationSections(String name, String path) {
        if (configs.get(name + ".yml").getConfigurationSection(path) == null) return null;
        return configs.get(name + ".yml").getConfigurationSection(path).getKeys(false);
    }

    public static void set(String name, String path, Object value) {
        configs.get(name + ".yml").set(path, value);
    }

    @SneakyThrows
    public static void save() {
        for (Map.Entry<String, FileConfiguration> config : configs.entrySet()) {
            config.getValue().save(new File(XG7PluginManager.getPlugin().getDataFolder(), config.getKey()));
        }
    }

    @SneakyThrows
    public static void save(String name) {
        configs.get(name).save(new File(XG7PluginManager.getPlugin().getDataFolder(), name + ".yml"));
    }


}
