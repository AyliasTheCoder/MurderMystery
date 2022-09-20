package me.aylias.plugins.dotwav.mm.timers.managers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.timers.CoinSpawner;

public class CoinSpawnerManager extends TimerManager {

    private final Game game;

    public CoinSpawnerManager(Game game) {
        super(() -> new CoinSpawner(game), true);
        this.game = game;
    }

    @Override
    public void failedRun() {

    }

    @Override
    public void successfulRun() {

    }
}
