package com.xg7plugins.api.utils;

import com.xg7plugins.api.XG7PluginManager;
import com.xg7plugins.api.Config;
import lombok.Setter;
import org.bukkit.Bukkit;

public class Log {
    @Setter
    public static boolean isEnabled = false;

    public static void severe(String message) {
        Bukkit.getConsoleSender().sendMessage("§c["+ XG7PluginManager.getPlugin().getName() + " ERROR] §r" + message);
    }

    public static void fine(String message) {
        if (isEnabled) Bukkit.getConsoleSender().sendMessage("§a["+ XG7PluginManager.getPlugin().getName() +" SUCSESS] §r" + message);
    }

    public static void info(String message) {
        if (isEnabled) Bukkit.getConsoleSender().sendMessage("§e["+ XG7PluginManager.getPlugin().getName() +" DEBUG] §r" + message);
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage("§6["+ XG7PluginManager.getPlugin().getName() +" ALERT] §r" + message);
    }

    public static void loading(String message) {
        Bukkit.getConsoleSender().sendMessage(Config.getString("config", "prefix") + message);
    }

}
