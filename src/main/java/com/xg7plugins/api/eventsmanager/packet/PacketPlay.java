package com.xg7plugins.api.eventsmanager.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;

@AllArgsConstructor
@Getter
public class PacketPlay {
    private Object packet;

    public Field getFiled(String name) throws NoSuchFieldException {
        Class<?> packetClass = packet.getClass();
        Field field = packetClass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }


}
