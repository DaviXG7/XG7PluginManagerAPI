package com.xg7plugins.api.eventsmanager;

import org.bukkit.event.EventPriority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Event {
    EnabledIf enabledIf() default @Event.EnabledIf();
    boolean isOnlyInWorld() default false;
    EventPriority priority() default EventPriority.NORMAL;
    Class<? extends org.bukkit.event.Event> event();

    @interface EnabledIf {
        String configName() default "null";
        String path() default "";
        boolean invert() default false;
    }
}
