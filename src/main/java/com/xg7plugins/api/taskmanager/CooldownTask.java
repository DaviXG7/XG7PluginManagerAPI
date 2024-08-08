package com.xg7plugins.api.taskmanager;


import lombok.Getter;

@Getter
public abstract class CooldownTask implements Task {

    private int cooldown;
    private int reamingSeconds;

    public CooldownTask(int cooldown) {
        this.cooldown = cooldown;
        this.reamingSeconds = cooldown;
    }

    @Override
    public long getDelay() {
        return 20;
    }
    @Override
    public abstract String getName();
    @Override
    public abstract void run();
    public abstract void onFinish();

    public void decrement() {
        this.reamingSeconds--;
    }

}
