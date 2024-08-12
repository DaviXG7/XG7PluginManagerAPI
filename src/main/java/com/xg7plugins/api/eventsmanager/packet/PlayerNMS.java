package com.xg7plugins.api.eventsmanager.packet;

import com.xg7plugins.api.XG7PluginManager;
import com.xg7plugins.api.utils.NMSUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public class PlayerNMS {

    private Player player;
    private Object craftPlayerHandle;
    private Object playerConnection;
    private Object networkManager;

    @SneakyThrows
    public static PlayerNMS cast(Player player) {
        Class<?> craftPlayerClass = NMSUtil.getCraftBukkitClass("entity.CraftPlayer");
        Object craftPlayer = craftPlayerClass.cast(player);

        Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
        if (XG7PluginManager.getVersion() < 17) {

            Object playerConnection = Arrays.stream(craftPlayerHandle.getClass().getFields()).filter(field -> {
                try {
                    return field.getType().getName().equals(NMSUtil.getNMSClass("PlayerConnection").getName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).findFirst().orElse(null).get(craftPlayerHandle);
            Object networkManager = Arrays.stream(playerConnection.getClass().getFields()).filter(field -> {
                try {
                    return field.getType().getName().equals(NMSUtil.getNMSClass("NetworkManager").getName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).findFirst().orElse(null).get(playerConnection);
            return new PlayerNMS(player, craftPlayerHandle, playerConnection, networkManager);
        }
        Object playerConnection = Arrays.stream(craftPlayerHandle.getClass().getFields()).filter(field -> field.getType().getName().equals("net.minecraft.server.network.PlayerConnection")).findFirst().orElse(null).get(craftPlayerHandle);
        Object networkManager = Arrays.stream(playerConnection.getClass().getDeclaredFields()).filter(field -> {
            field.setAccessible(true);
            return field.getType().getName().equals("net.minecraft.network.NetworkManager");
        }).findFirst().orElse(null).get(playerConnection);
        return new PlayerNMS(player, craftPlayerHandle, playerConnection, networkManager);
    }



}
