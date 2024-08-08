package com.xg7plugins.api;

import com.xg7plugins.api.commandsmanager.Command;
import com.xg7plugins.api.commandsmanager.CommandManager;
import com.xg7plugins.api.eventsmanager.EventManager;
import com.xg7plugins.api.utils.Metrics;
import com.xg7plugins.xg7menus.api.XG7Menus;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class XG7PluginManager {
    @Getter
    private static JavaPlugin plugin;

    @Getter
    @Setter
    private static List<String> worldsEnabled = plugin.getConfig().getStringList("enabled-worlds");
    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        javaPlugin.getServer().getPluginManager().registerEvents(new EventManager(), javaPlugin);
        XG7Menus.inicialize(javaPlugin);
    }
    public static void initMetrics(int serviceId) {
        new Metrics(plugin, serviceId);
    }
    public static void registerEvents(Object... events) {
        new EventManager().registerEvents(plugin, events);
    }
    public static void registerCommands(Class<? extends Command>... commands) {
        new CommandManager().init(commands);
    }

}
