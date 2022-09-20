package me.aylias.plugins.dotwav.mm.timers.managers;

import org.bukkit.scheduler.BukkitRunnable;

public interface TimerProvider {

    public BukkitRunnable getTimer();
}
