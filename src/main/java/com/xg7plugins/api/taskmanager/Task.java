package com.xg7plugins.api.taskmanager;

public interface Task {
    String getName();
    long getDelay();
    void run();
}
