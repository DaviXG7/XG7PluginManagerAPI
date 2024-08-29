package com.xg7plugins.api.utils.placeholders;

import org.bukkit.entity.Player;

@Fun
public interface Placeholder {

    String name();
    String returns(Player player);

}
