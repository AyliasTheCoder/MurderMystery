package me.aylias.plugins.dotwav.mm.timers.managers;

import me.aylias.plugins.dotwav.mm.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ServiceLoader;

public abstract class TimerManager {

    private final TimerProvider provider;
    public BukkitRunnable timer;

    public TimerManager(TimerProvider provider, boolean runNow) {
        this.provider = provider;
        if (runNow) {
            run();
        }
    }

    public final void run() {
        if (isRunning()) {
            failedRun();
        } else {
            timer = provider.getTimer();
            timer.runTaskTimer(Main.getInstance(), 0, 0);
            successfulRun();
        }
    }

    public abstract void failedRun();

    public abstract void successfulRun();

    public boolean isRunning() {
        if (timer == null) return false;

        return !timer.isCancelled();
    }

    public void cancel() {
        if (isRunning()) {
            timer.cancel();
        }

        timer = null;
    }
}
