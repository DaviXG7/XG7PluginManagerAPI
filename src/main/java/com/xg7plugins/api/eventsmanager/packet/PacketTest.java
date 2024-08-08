package com.xg7plugins.api.eventsmanager.packet;


public class PacketTest {
    @PacketEvent(packetName = "PacketPlayOutChat")
    public Object aEvent(Object packetPlayOutChat) {

        return packetPlayOutChat;
    }
}
