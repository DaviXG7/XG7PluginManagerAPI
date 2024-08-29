package com.xg7plugins.api.eventsmanager.defaultevents;

import com.xg7plugins.api.eventsmanager.packet.PacketEvent;
import com.xg7plugins.xg7menus.api.utils.NMSUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginAntiTab {
    @PacketEvent(packetName = "Packet")
    public void antiTab(Player player, Object packet) {
        Class<?> packetPlayOutTabCompleteClass = NMSUtil.getNMSClass("PacketPlayOutTabComplete");
        Field suggestionsField = packetPlayOutTabCompleteClass.getDeclaredField("a");
        suggestionsField.setAccessible(true);

        Object suggestions = suggestionsField.get(packet);

        if (suggestions instanceof String[]) {
            String[] suggestionsArray = (String[]) suggestions;
            if (player.hasPermission(PermissionType.ANTITAB_PLUGIN_BYPASS.getPerm())) return packet;

            for (String commands : CommandManager.getCommands().stream().flatMap(cmd -> {
                        List<String> combined = new ArrayList<>();
                        combined.add("/" + cmd.getName());
                        combined.addAll(cmd.getAliasses().stream().map(alias -> "/" + alias).collect(Collectors.toList()));
                        return combined.stream();
                    })
                    .collect(Collectors.toList()))
            {
                suggestionsArray = java.util.Arrays.stream(suggestionsArray)
                        .filter(suggestion -> !suggestion.startsWith(commands))
                        .toArray(String[]::new);

            }
            suggestionsField.set(packet, suggestionsArray);
        }
        return packet;
    }
}
