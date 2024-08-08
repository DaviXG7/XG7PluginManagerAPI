package com.xg7plugins.api.taskmanager;

import com.xg7plugins.api.XG7PluginManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final static HashMap<String, Integer> tasksRunning = new HashMap<>();

    private static void init(Task... tasks) {

    }

    public static void addTask(Task task) {
        int taskid = Bukkit.getServer().getScheduler().runTaskTimer(
                XG7PluginManager.getPlugin(),
                task::run,
                0,
                task.getDelay()
        ).getTaskId();

        tasksRunning.put(task.getName(), taskid);
    }

    public static void addTaskAsync(Task task) {
        int taskid = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(
                XG7PluginManager.getPlugin(),
                task::run,
                0,
                task.getDelay()
        ).getTaskId();

        tasksRunning.put(task.getName(), taskid);
    }

    public static void addCooldownTask(CooldownTask task) {
        int taskid = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(
                XG7PluginManager.getPlugin(),
                () -> {
                    task.run();
                    task.decrement();
                },
                0,
                task.getDelay()
        ).getTaskId();
        Bukkit.getScheduler().runTaskLater(XG7PluginManager.getPlugin(), () -> {
            cancelTask(task.getName());
            task.onFinish();
        }, task.getCooldown() * 20L);
    }

    public static void cancelTask(String name) {
        if (tasksRunning.get(name) == null) return;
        Bukkit.getScheduler().cancelTask(tasksRunning.get(name));
        tasksRunning.remove(name);
    }
}
