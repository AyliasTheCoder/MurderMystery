package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingTimer extends BukkitRunnable {

    private final Game game;
    private final String message;

    public EndingTimer(Game game, String message) {
        this.game = game;
        this.message = message;
    }

    @Override
    public void run() {
        game.tpToLobby(message);
    }
}
