package com.xg7plugins.api.eventsmanager.packet;

import com.xg7plugins.api.Config;
import com.xg7plugins.api.utils.NMSUtil;
import io.netty.channel.*;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketEventManager1_17__1_2x implements Listener {

    private static final List<Object> events = new ArrayList<>();


    public static void registerEvents(Object... event) {
        events.addAll(Arrays.asList(event));
        for (Object e : event) {
            for (Method method : e.getClass().getMethods()) {
                if (!method.isAnnotationPresent(PacketEvent.class)) continue;
                PacketEvent event1 = method.getAnnotation(PacketEvent.class);
                if (Config.getConfig(event1.enabledIf().configName()) != null) {
                    if (!Boolean.logicalXor(Config.getBoolean(event1.enabledIf().configName(), event1.enabledIf().path()), event1.enabledIf().invert()))
                        events.add(e);
                }
            }
        }
        Bukkit.getOnlinePlayers().forEach(PacketEventManager1_17__1_2x::create);
    }


    @SneakyThrows
    public static void create(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet)
                    throws Exception {

                Object modPacket = packet;

                for (Object object : events) {
                    loop: for (Method method : object.getClass().getMethods()) {
                        if (method.isAnnotationPresent(PacketEvent.class)) {
                            PacketEvent event = method.getAnnotation(PacketEvent.class);
                            if (Config.getConfig(event.enabledIf().configName()) != null) {
                                if (Boolean.logicalXor(Config.getBoolean(event.enabledIf().configName(), event.enabledIf().path()), event.enabledIf().invert())) {
                                    continue loop;
                                }
                            }
                            if (packet.getClass().getName().endsWith(event.packetName()))
                                modPacket = method.invoke(object,  player, packet);
                        }
                    }
                }

                super.channelRead(context, packet);
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet,
                              ChannelPromise channelPromise) throws Exception {

                Object modPacket = packet;

                for (Object object : events) {
                    loop: for (Method method : object.getClass().getMethods()) {
                        if (method.isAnnotationPresent(PacketEvent.class)) {
                            PacketEvent event = method.getAnnotation(PacketEvent.class);
                            if (Config.getConfig(event.enabledIf().configName()) != null) {
                                if (Boolean.logicalXor(Config.getBoolean(event.enabledIf().configName(), event.enabledIf().path()), event.enabledIf().invert())) {
                                    continue loop;
                                }
                            }
                            if (packet.getClass().getName().endsWith(event.packetName()))
                                modPacket = method.invoke(object,  player, packet);
                        }
                    }
                }

                super.write(context, packet, channelPromise);
            }
        };

        PlayerNMS playerNMS = PlayerNMS.cast(player);
        Channel channel = (Channel) Arrays.stream(playerNMS.getNetworkManager().getClass().getDeclaredFields()).filter(field -> {
            field.setAccessible(true);
            return field.getType().getName().equals(Channel.class.getName());
        }).findFirst().orElse(null).get(playerNMS.getNetworkManager());


        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }
    public static void stopEvent(Player player) {
        try {
            PlayerNMS playerNMS = PlayerNMS.cast(player);
            Channel channel = (Channel) Arrays.stream(playerNMS.getNetworkManager().getClass().getDeclaredFields()).filter(field -> {
                field.setAccessible(true);
                return field.getType().getName().equals(Channel.class.getName());
            }).findFirst().orElse(null).get(playerNMS.getNetworkManager());

            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        create(event.getPlayer());
    }
    @EventHandler
    public void leaveEvent(PlayerQuitEvent event) {
        stopEvent(event.getPlayer());
    }
}
