package com.xg7plugins.api.databasemanager;

import com.xg7plugins.api.langManager.LangType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DefaultPlayerData implements Entity {

    private Timestamp firstJoin;
    @PKey
    private UUID id;
    private LangType lang;

    public DefaultPlayerData() {}


}
