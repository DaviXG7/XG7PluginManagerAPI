package com.xg7plugins.api.eventsmanager.packet;

import com.xg7plugins.api.Config;
import com.xg7plugins.api.utils.NMSUtil;
import io.netty.channel.*;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PacketEventManager {

    private static final List<Method> events = new ArrayList<>();


    public static void registerEvents(Object... event) {
        for (Object e : event) {
            for (Method method : e.getClass().getMethods()) {
                if (!method.isAnnotationPresent(PacketEvent.class)) continue;
                PacketEvent event1 = method.getAnnotation(PacketEvent.class);
                if (Config.getConfig(event1.enabledIf().configName()) != null) {
                    if (Boolean.logicalXor(Config.getBoolean(event1.enabledIf().configName(), event1.enabledIf().path()), event1.enabledIf().invert()))
                        continue;
                }
                events.add(method);
            }
        }
    }


    @SneakyThrows
    public static void create(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet)
                    throws Exception {

                Object modPacket = packet;

                for (Method method : events) {
                    PacketEvent event = method.getAnnotation(PacketEvent.class);
                    if (event.packetName().equals(packet.getClass().getName())) modPacket = method.invoke(packet);
                }

                super.channelRead(context, packet);
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet,
                              ChannelPromise channelPromise) throws Exception {

                Object modPacket = packet;

                for (Method method : events) {
                    PacketEvent event = method.getAnnotation(PacketEvent.class);
                    if (event.packetName().equals(packet.getClass().getName())) modPacket = method.invoke(packet);
                }

                super.write(context, packet, channelPromise);
            }
        };

            Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);

        Object playerConnection = Arrays.stream(craftPlayerHandle.getClass().getFields()).filter(field -> {
            try {
                return field.getType().getName().equals(NMSUtil.getNMSClass("PlayerConnection").getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()).get(0).get(craftPlayerHandle);
        Object networkManager = Arrays.stream(playerConnection.getClass().getFields()).filter(field -> {
            try {
                return field.getType().getName().equals(NMSUtil.getNMSClass("NetworkManager").getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()).get(0).get(playerConnection);
            Channel channel = (Channel) Arrays.stream(networkManager.getClass().getFields()).filter(field -> field.getType().getName().equals(Channel.class.getName())).collect(Collectors.toList()).get(0).get(networkManager);


            ChannelPipeline channelPipeline = channel.pipeline();
            channelPipeline.addBefore("packet_handler", player.getName(),
                    channelDuplexHandler);
    }
    public static void stopEvent(Player player) {
        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1].replace(")", "")) <= 13) {
            try {
                Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
                Object craftPlayer = craftPlayerClass.cast(player);

                Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
                Object playerConnection = Arrays.stream(craftPlayerHandle.getClass().getFields()).filter(field -> {
                    try {
                        return field.getType().getName().equals(NMSUtil.getNMSClass("PlayerConnection").getName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()).get(0).get(craftPlayerHandle);
                Object networkManager = Arrays.stream(playerConnection.getClass().getFields()).filter(field -> {
                    try {
                        return field.getType().getName().equals(NMSUtil.getNMSClass("NetworkManager").getName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()).get(0).get(playerConnection);
                Channel channel = (Channel) Arrays.stream(networkManager.getClass().getFields()).filter(field -> field.getType().getName().equals(Channel.class.getName())).collect(Collectors.toList()).get(0).get(networkManager);

                channel.eventLoop().submit(() -> {
                    channel.pipeline().remove(player.getName());
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
