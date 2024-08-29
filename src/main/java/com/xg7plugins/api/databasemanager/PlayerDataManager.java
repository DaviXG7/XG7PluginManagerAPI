package com.xg7plugins.api.databasemanager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerDataManager {

    @Getter
    private static Cache<UUID, DefaultPlayerData> playerDataCache;

    public static void initPlayerData(long cooldownCache) {
        EntityProcessor.createTableOf(DefaultPlayerData.class);
        playerDataCache = Caffeine.newBuilder().expireAfterWrite(cooldownCache, TimeUnit.MINUTES).build();
    }

    public static void addCachePlayerData(DefaultPlayerData playerData) {
        playerDataCache.put(playerData.getId(), playerData);
    }

}
