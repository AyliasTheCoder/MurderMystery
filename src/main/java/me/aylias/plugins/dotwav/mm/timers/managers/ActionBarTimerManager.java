package me.aylias.plugins.dotwav.mm.timers.managers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.timers.ActionBarTimer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ServiceLoader;

public class ActionBarTimerManager extends TimerManager {

    private final Game game;

    public ActionBarTimerManager(Game game) {
        super(() -> new ActionBarTimer(game), true);
        this.game = game;
    }

    @Override
    public void failedRun() {

    }

    @Override
    public void successfulRun() {

    }
}
