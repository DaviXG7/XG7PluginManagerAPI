package com.xg7plugins.api.eventsmanager.packet;

import com.xg7plugins.api.eventsmanager.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})

public @interface PacketEvent {

    Event.EnabledIf enabledIf() default @Event.EnabledIf();
    boolean isOnlyInWorld() default false;
    String packetName();

}
