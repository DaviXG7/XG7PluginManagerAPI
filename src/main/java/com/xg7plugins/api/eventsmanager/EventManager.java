package com.xg7plugins.api.eventsmanager;

import com.xg7plugins.api.XG7PluginManager;
import com.xg7plugins.api.Config;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventManager implements Listener {

    private static final List<Object> events = new ArrayList<>();

    public static void reload() {
        HandlerList.unregisterAll(XG7PluginManager.getPlugin());
        new EventManager().registerEvents(XG7PluginManager.getPlugin(), events);
    }

    public void registerEvents(JavaPlugin plugin, Object... event) {
        for (Object e : event) {
            for (Method method : e.getClass().getMethods()) {
                if (!method.isAnnotationPresent(Event.class)) continue;
                Event event1 = method.getAnnotation(Event.class);
                if (Config.getConfig(event1.enabledIf().configName()) != null) {
                    if (Boolean.logicalXor(Config.getBoolean(event1.enabledIf().configName(), event1.enabledIf().path()), event1.enabledIf().invert())) continue;
                }
                plugin.getServer().getPluginManager().registerEvent(
                        event1.event(),
                        this,
                        event1.priority(),
                        (listener, event2) -> {
                            try {
                                if (method.getAnnotation(Event.class).isOnlyInWorld()) {
                                    if (event2 instanceof PlayerEvent) {
                                        PlayerEvent playerEvent = (PlayerEvent) event2;
                                        if (!XG7PluginManager.getWorldsEnabled().contains(playerEvent.getPlayer().getWorld().getName())) return;
                                    }
                                    if (event2 instanceof WorldEvent) {
                                        WorldEvent worldEvent = (WorldEvent) event2;
                                        if (!XG7PluginManager.getWorldsEnabled().contains(worldEvent.getWorld().getName())) return;
                                    }
                                    if (event2 instanceof BlockEvent) {
                                        BlockEvent blockEvent = (BlockEvent) event2;
                                        if (!XG7PluginManager.getWorldsEnabled().contains(blockEvent.getBlock().getWorld().getName())) return;
                                    }
                                }
                                method.invoke(e, event2);
                            } catch (Exception exception) {
                                throw new EventException(exception);
                            }
                        },
                        plugin

                );
            }

            events.add(e);
        }
    }


}
