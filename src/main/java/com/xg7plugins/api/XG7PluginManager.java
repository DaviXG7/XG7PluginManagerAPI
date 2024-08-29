package com.xg7plugins.api;

import com.xg7plugins.api.commandsmanager.Command;
import com.xg7plugins.api.commandsmanager.CommandManager;
import com.xg7plugins.api.databasemanager.DBManager;
import com.xg7plugins.api.eventsmanager.EventManager;
import com.xg7plugins.api.eventsmanager.packet.PacketEventManager1_17__1_2x;
import com.xg7plugins.api.eventsmanager.packet.PacketEventManager1_8__1_16;
import com.xg7plugins.api.eventsmanager.packet.PacketEventManager1_7;
import com.xg7plugins.api.taskmanager.Task;
import com.xg7plugins.api.taskmanager.TaskManager;
import com.xg7plugins.api.utils.Conversation;
import com.xg7plugins.api.utils.Metrics;
import com.xg7plugins.xg7menus.api.XG7Menus;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class XG7PluginManager {
    @Getter
    private static JavaPlugin plugin;
    @Getter
    private static int version;
    @Getter
    @Setter
    private static String pluginPrefix;
    @Getter
    @Setter
    private static List<String> worldsEnabled;

    public static void init(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
        version = Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", ""));
        worldsEnabled = plugin.getConfig().getStringList("worlds");
        pluginPrefix = plugin.getConfig().getString("plugin-prefix");
        plugin.getServer().getPluginManager().registerEvents(new EventManager(), plugin);
        if (version < 8) plugin.getServer().getPluginManager().registerEvents(new PacketEventManager1_7(), plugin);
        else if (version > 16) plugin.getServer().getPluginManager().registerEvents(new PacketEventManager1_17__1_2x(), plugin);
        else plugin.getServer().getPluginManager().registerEvents(new PacketEventManager1_8__1_16(), plugin);
        XG7Menus.inicialize(plugin);

    }
    public static void initMetrics(int serviceId) {
        new Metrics(plugin, serviceId);
    }
    public static void registerEvents(Object... events) {
        new EventManager().registerEvents(plugin, events);
    }
    public static void registerPacketEvents(Object... events) {
        if (version < 8) {
            PacketEventManager1_7.registerEvents(events);
            return;
        }
        if (version > 16) {
            PacketEventManager1_17__1_2x.registerEvents(events);
            return;
        }
        PacketEventManager1_8__1_16.registerEvents(events);
    }
    public static void initTasks(Task... tasks) {
        TaskManager.init(tasks);
    }
    public static void registerCommands(Command... commands) {
        new CommandManager().init(commands);
    }

    public static void disable() {
        if (version < 8) {
            Bukkit.getOnlinePlayers().forEach(PacketEventManager1_7::stopEvent);
            return;
        }
        if (version > 16) {
            Bukkit.getOnlinePlayers().forEach(PacketEventManager1_17__1_2x::stopEvent);
            return;
        }
        Bukkit.getOnlinePlayers().forEach(PacketEventManager1_8__1_16::stopEvent);

        DBManager.closeConnection();

        HandlerList.unregisterAll(XG7PluginManager.getPlugin());
    }
}
