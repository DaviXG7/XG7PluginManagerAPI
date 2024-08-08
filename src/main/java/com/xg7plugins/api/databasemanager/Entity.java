package com.xg7plugins.api.databasemanager;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Entity {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface PKey {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface FKey {}
}
