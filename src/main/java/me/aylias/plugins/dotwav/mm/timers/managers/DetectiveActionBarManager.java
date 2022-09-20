package me.aylias.plugins.dotwav.mm.timers.managers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.timers.DetectiveActionBar;

public class DetectiveActionBarManager extends TimerManager {

    private final Game game;

    public DetectiveActionBarManager(Game game) {
        super(() -> new DetectiveActionBar(game), true);
        this.game = game;
    }

    @Override
    public void failedRun() {

    }

    @Override
    public void successfulRun() {

    }
}
